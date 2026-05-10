package com.gdsc.recyclr.data.ml

import android.content.Context
import android.graphics.Bitmap
import com.gdsc.recyclr.domain.ml.WasteDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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
 * ImageNet (MobileNet float de test TFLite) + mapping heuristique vers les types Recyclr.
 * Repli sur [FakeWasteDetector] si le modèle / les labels ne sont pas utilisables.
 */
@Singleton
class TensorflowLiteWasteDetector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fakeWasteDetector: FakeWasteDetector
) : WasteDetector {

    private val interpreter: Interpreter? = runCatching {
        val modelBuffer = FileUtil.loadMappedFile(context, MODEL_ASSET)
        Interpreter(modelBuffer, Interpreter.Options().apply { setNumThreads(4) })
    }.getOrNull()

    private val imagenetLabels: List<String> = runCatching {
        context.assets.open(LABELS_ASSET).bufferedReader().readLines().map { it.trim() }
    }.getOrDefault(emptyList())

    override suspend fun detectItemTypeFromBitmap(bitmap: Bitmap): Result<String> = withContext(Dispatchers.Default) {
        val interp = interpreter
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

        val probs = FloatArray(classCount)
        outputBuffer.buffer.rewind()
        outputBuffer.buffer.asFloatBuffer().get(probs)

        val labelCount = minOf(classCount, imagenetLabels.size)
        if (labelCount == 0) {
            return@withContext fakeWasteDetector.detectItemTypeFromBitmap(bitmap)
        }

        val topIdx = probs.indices.take(labelCount).maxByOrNull { probs[it] } ?: 0
        val imagenetLabel = imagenetLabels.getOrNull(topIdx).orEmpty()
        Result.success(mapImagenetLabelToWaste(imagenetLabel))
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
