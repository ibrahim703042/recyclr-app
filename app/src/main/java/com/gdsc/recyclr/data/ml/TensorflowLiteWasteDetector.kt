package com.gdsc.recyclr.data.ml

import android.content.Context
import android.graphics.Bitmap
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.ml.WasteDetection
import com.gdsc.recyclr.domain.ml.WasteDetector
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.exp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inférence TensorFlow Lite (modèle dans [MODEL_ASSET]) + mapping vers les types Recyclr.
 * Optionnellement, [syncHostedModelIfAvailable] remplace l’interpréteur par un fichier
 * téléchargé via Firebase ML Model Downloader (voir doc « use custom models »).
 * Repli sur [FakeWasteDetector] si le modèle ou les labels ne sont pas utilisables.
 */
@Singleton
class TensorflowLiteWasteDetector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fakeWasteDetector: FakeWasteDetector,
) : WasteDetector {

    private val interpreterLock = Any()
    private var interpreter: Interpreter? = loadAssetInterpreter()

    private val imagenetLabels: List<String> = runCatching {
        context.assets.open(LABELS_ASSET).bufferedReader().readLines().map { it.trim() }
    }.getOrDefault(emptyList())

    private val hostedSyncStarted = AtomicBoolean(false)

    private fun loadAssetInterpreter(): Interpreter? = runCatching {
        val modelBuffer = FileUtil.loadMappedFile(context, MODEL_ASSET)
        val opts = Interpreter.Options().apply {
            setNumThreads(4)
            runCatching { setUseXNNPACK(true) }
        }
        Interpreter(modelBuffer, opts)
    }.getOrNull()

    private fun interpreterOptions() = Interpreter.Options().apply {
        setNumThreads(4)
        runCatching { setUseXNNPACK(true) }
    }

    private fun getInterpreter(): Interpreter? = synchronized(interpreterLock) { interpreter }

    override suspend fun syncHostedModelIfAvailable() {
        if (!hostedSyncStarted.compareAndSet(false, true)) return
        val modelName = context.getString(R.string.firebase_waste_model_name).trim()
        if (modelName.isEmpty()) return
        withContext(Dispatchers.IO) {
            runCatching {
                val conditions = CustomModelDownloadConditions.Builder().build()
                val customModel = FirebaseModelDownloader.getInstance()
                    .getModel(
                        modelName,
                        DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                        conditions,
                    )
                    .await()
                val file = customModel.file ?: return@runCatching
                val newInterpreter = runCatching {
                    Interpreter(file, interpreterOptions())
                }.getOrNull() ?: return@runCatching
                synchronized(interpreterLock) {
                    interpreter?.close()
                    interpreter = newInterpreter
                }
            }
        }
    }

    override suspend fun detectItemTypeFromBitmap(bitmap: Bitmap): Result<WasteDetection> =
        withContext(Dispatchers.Default) {
            if (bitmap.width < 16 || bitmap.height < 16) {
                return@withContext Result.failure(
                    IllegalArgumentException("Image trop petite pour l'analyse"),
                )
            }

            val interp = getInterpreter()
            if (interp == null || imagenetLabels.isEmpty()) {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            val inTensor = interp.getInputTensor(0)
            val inShape = inTensor.shape()
            if (inShape.size != 4 || inShape[3] != 3) {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }
            val h = inShape[1]
            val w = inShape[2]
            if (h <= 0 || w <= 0) {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            val outTensor = interp.getOutputTensor(0)
            val outShape = outTensor.shape()
            val classCount = if (outShape.size >= 2) outShape[1] else {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }
            if (classCount <= 0) {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            val processed = when (inTensor.dataType()) {
                DataType.FLOAT32 -> {
                    val imageProcessor = ImageProcessor.Builder()
                        .add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
                        .add(CastOp(DataType.FLOAT32))
                        .add(NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                        .build()
                    val tensorImage = TensorImage(DataType.UINT8)
                    tensorImage.load(bitmap)
                    imageProcessor.process(tensorImage)
                }
                DataType.UINT8 -> {
                    val imageProcessor = ImageProcessor.Builder()
                        .add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
                        .build()
                    val tensorImage = TensorImage(DataType.UINT8)
                    tensorImage.load(bitmap)
                    imageProcessor.process(tensorImage)
                }
                else -> return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            val outDataType = outTensor.dataType()
            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, classCount), outDataType)

            runCatching {
                processed.buffer.rewind()
                outputBuffer.buffer.rewind()
                interp.run(processed.buffer, outputBuffer.buffer)
            }.onFailure {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            if (outDataType != DataType.FLOAT32) {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            val logits = FloatArray(classCount)
            outputBuffer.buffer.rewind()
            outputBuffer.buffer.asFloatBuffer().get(logits)

            val labelCount = minOf(classCount, imagenetLabels.size)
            if (labelCount == 0) {
                return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
            }

            val probs = softmax1D(logits, labelCount)
            val topIdx = (0 until labelCount).maxByOrNull { probs[it] } ?: 0
            val confidence = probs[topIdx].coerceIn(0f, 1f)
            val imagenetLabel = imagenetLabels.getOrNull(topIdx).orEmpty()
            val itemType = mapImagenetLabelToWaste(imagenetLabel)
            Result.success(WasteDetection(itemType = itemType, confidence = confidence))
        }

    /** Softmax stable sur les [length] premières composantes (logits ou scores bruts). */
    private fun softmax1D(values: FloatArray, length: Int): FloatArray {
        val n = minOf(values.size, length)
        var max = Float.NEGATIVE_INFINITY
        for (i in 0 until n) {
            if (values[i] > max) max = values[i]
        }
        var sum = 0.0
        val expVals = DoubleArray(n)
        for (i in 0 until n) {
            val e = exp((values[i] - max).toDouble())
            expVals[i] = e
            sum += e
        }
        if (sum <= 0.0) return FloatArray(n) { 1f / n }
        val inv = 1.0 / sum
        return FloatArray(n) { i -> (expVals[i] * inv).toFloat() }
    }

    private fun mapImagenetLabelToWaste(label: String): String {
        val l = label.lowercase()
        return when {
            l.contains("battery") || l.contains("screen") || l.contains("monitor") ||
                l.contains("laptop") || l.contains("computer") || l.contains("keyboard") ||
                l.contains("electric") || l.contains("modem") || l.contains("cellular") ||
                l.contains("hand-held") -> "Hazardous"

            l.contains("chair") || l.contains("table") || l.contains("desk") || l.contains("bookcase") ||
                l.contains("wardrobe") || l.contains("studio couch") || l.contains("chest") ||
                l.contains("bed") || l.contains("dining table") -> "Furniture"

            l.contains("carton") || l.contains("paper") || l.contains("envelope") || l.contains("book") ||
                l.contains("notebook") || l.contains("newspaper") || l.contains("packet") -> "Paper"

            l.contains("bottle") || l.contains("plastic") || l.contains("bucket") || l.contains("cup") ||
                l.contains("jug") || l.contains("can") || l.contains("water") || l.contains("soap") -> "Plastic"

            else -> "Plastic"
        }
    }

    companion object {
        private const val MODEL_ASSET = "ml/waste_classifier.tflite"
        private const val LABELS_ASSET = "ml/labels_imagenet.txt"
        private const val IMAGE_MEAN = 127.5f
        private const val IMAGE_STD = 127.5f
    }
}
