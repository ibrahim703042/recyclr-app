package com.gdsc.recyclr.screens.support

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gdsc.recyclr.domain.model.ChatMessage
import com.gdsc.recyclr.domain.model.ChatMessageType
import java.text.DateFormat
import java.util.Date

// Brand teal used throughout
private val BrandTeal = Color(0xFF1D9E75)
private val BrandTealDark = Color(0xFF0F6E56)
private val BubbleMine = BrandTeal
private val BubbleMineText = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatScreen(
    onBack: () -> Unit,
    viewModel: SupportChatViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val typingStatuses by viewModel.typingStatuses.collectAsStateWithLifecycle()
    var draft by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Quick replies shown until the user sends one
    var showQuickReplies by remember { mutableStateOf(true) }
    val quickReplies = listOf("Pickup issue", "Billing question", "App not working")

    // Attachment preview state
    var attachedUri by remember { mutableStateOf<Uri?>(null) }
    var attachedName by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            attachedUri = it
            attachedName = "image.jpg"
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            attachedUri = it
            attachedName = "document.pdf"
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.sendMediaMessage(it, "video/mp4", ChatMessageType.VIDEO) }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.markSupportThreadSeenOnLeave() }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) scrollState.scrollTo(scrollState.maxValue)
    }

    val isTyping = typingStatuses.values.any { it == "typing" }
    val isRecording = typingStatuses.values.any { it == "recording" }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            ChatTopBar(
                isTyping = isTyping,
                isRecording = isRecording,
                onBack = onBack,
            )
        },
        bottomBar = {
            ChatInputBar(
                draft = draft,
                onDraftChange = {
                    draft = it
                    viewModel.setTypingStatus(if (it.isNotEmpty()) "typing" else "none")
                },
                attachedUri = attachedUri,
                attachedName = attachedName,
                onRemoveAttachment = { attachedUri = null; attachedName = "" },
                onSend = {
                    if (attachedUri != null) {
                        viewModel.sendMediaMessage(attachedUri!!, "image/jpeg", ChatMessageType.IMAGE)
                        attachedUri = null
                        attachedName = ""
                    }
                    if (draft.isNotBlank()) {
                        viewModel.sendUserMessage(draft)
                        draft = ""
                        showQuickReplies = false
                    }
                },
                onAttachImage = { imagePicker.launch("image/*") },
                onAttachFile = { filePicker.launch("*/*") },
                onAttachVideo = { videoPicker.launch("video/*") },
                onRecord = { /* TODO: start audio recording */ },
                onTypingStatus = viewModel::setTypingStatus,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Agent intro card
            AgentCard()

            // Date divider
            DateDivider(label = "Today")

            // Messages
            messages.forEach { msg ->
                ChatBubble(msg)
            }

            // Typing indicator
            if (isTyping || isRecording) {
                TypingIndicator(isRecording = isRecording)
            }

            // Quick replies (shown after first bot message, before user sends one)
            if (showQuickReplies && messages.isNotEmpty()) {
                QuickReplies(
                    replies = quickReplies,
                    onSelect = { reply ->
                        viewModel.sendUserMessage(reply)
                        showQuickReplies = false
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    isTyping: Boolean,
    isRecording: Boolean,
    onBack: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            // Avatar circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandTeal),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Recyclr Support",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                val statusText = when {
                    isRecording -> "recording audio…"
                    isTyping -> "typing…"
                    else -> null
                }
                if (statusText != null) {
                    Text(
                        statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandTeal,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(BrandTeal)
                        )
                        Text(
                            "Online · typically replies in minutes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    }
}

// ─────────────────────────────────────────────
// Agent intro card
// ─────────────────────────────────────────────

@Composable
private fun AgentCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BrandTeal.copy(alpha = 0.10f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BrandTeal),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(
                    "Recyclr Support Team",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    "🟢 Online · Typically replies within minutes",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandTeal,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Date divider
// ─────────────────────────────────────────────

@Composable
private fun DateDivider(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

// ─────────────────────────────────────────────
// Quick replies
// ─────────────────────────────────────────────

@Composable
private fun QuickReplies(replies: List<String>, onSelect: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        replies.forEach { reply ->
            AssistChip(
                onClick = { onSelect(reply) },
                label = { Text(reply, style = MaterialTheme.typography.labelMedium) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color.Transparent,
                    labelColor = BrandTeal,
                ),
                border = AssistChipDefaults.assistChipBorder(
                    borderColor = BrandTeal,
                    enabled = true,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ─────────────────────────────────────────────
// Typing indicator
// ─────────────────────────────────────────────

@Composable
private fun TypingIndicator(isRecording: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val delays = listOf(0, 200, 400)
    val alphas = delays.map { delay ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = LinearEasing, delayMillis = delay),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "dot$delay",
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isRecording) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(16.dp))
                    Text("recording audio…", style = MaterialTheme.typography.labelSmall, color = BrandTeal)
                } else {
                    alphas.forEach { alpha ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BrandTeal.copy(alpha = alpha.value))
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Chat bubble
// ─────────────────────────────────────────────

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isMine = msg.fromUser
    val align = if (isMine) Alignment.End else Alignment.Start

    val time = remember(msg.sentAtMillis) {
        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(msg.sentAtMillis))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align,
    ) {
        Text(
            text = if (isMine) "You" else "Support Team",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        )

        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically { it / 2 },
        ) {
            when (msg.type) {
                ChatMessageType.TEXT -> TextBubble(msg, isMine, time)
                ChatMessageType.IMAGE -> ImageBubble(msg, isMine, time)
                ChatMessageType.AUDIO -> AudioBubble(msg, isMine, time)
                ChatMessageType.VIDEO -> VideoBubble(msg, isMine, time)
            }
        }
    }
}

// Text bubble
@Composable
private fun TextBubble(msg: ChatMessage, isMine: Boolean, time: String) {
    val bgColor = if (isMine) BubbleMine else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) BubbleMineText else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(
        topStart = 18.dp, topEnd = 18.dp,
        bottomStart = if (isMine) 18.dp else 4.dp,
        bottomEnd = if (isMine) 4.dp else 18.dp,
    )
    Surface(color = bgColor, shape = shape, tonalElevation = 0.dp, modifier = Modifier.widthIn(max = 300.dp)) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(msg.body, style = MaterialTheme.typography.bodyMedium, color = textColor, lineHeight = 20.sp)
            Spacer(Modifier.height(4.dp))
            BubbleMeta(time, isMine, textColor)
        }
    }
}

// Image bubble
@Composable
private fun ImageBubble(msg: ChatMessage, isMine: Boolean, time: String) {
    val bgColor = if (isMine) BubbleMine else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) BubbleMineText else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(
        topStart = 16.dp, topEnd = 16.dp,
        bottomStart = if (isMine) 16.dp else 4.dp,
        bottomEnd = if (isMine) 4.dp else 16.dp,
    )
    Surface(color = bgColor, shape = shape, tonalElevation = 0.dp, modifier = Modifier.widthIn(max = 260.dp)) {
        Column(Modifier.padding(5.dp)) {
            if (msg.fileUrl != null) {
                AsyncImage(
                    model = msg.fileUrl,
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text("Photo", style = MaterialTheme.typography.labelSmall, color = textColor, modifier = Modifier.weight(1f))
                BubbleMeta(time, isMine, textColor)
            }
        }
    }
}

// Audio bubble
@Composable
private fun AudioBubble(msg: ChatMessage, isMine: Boolean, time: String) {
    val bgColor = if (isMine) BubbleMine else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) BubbleMineText else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(
        topStart = 18.dp, topEnd = 18.dp,
        bottomStart = if (isMine) 18.dp else 4.dp,
        bottomEnd = if (isMine) 4.dp else 18.dp,
    )
    val barHeights = remember { listOf(6, 10, 14, 18, 22, 16, 12, 8, 20, 24, 18, 14, 10, 16, 22) }

    Surface(color = bgColor, shape = shape, tonalElevation = 0.dp, modifier = Modifier.widthIn(max = 280.dp)) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Play button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(textColor.copy(alpha = 0.15f))
                        .clickable { /* TODO: play */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = textColor, modifier = Modifier.size(20.dp))
                }
                // Waveform
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    barHeights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(h.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(textColor.copy(alpha = 0.6f))
                        )
                    }
                }
                // Duration
                val dur = msg.durationMillis?.let { "${it / 1000}s" } ?: "0:12"
                Text(dur, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.8f))
            }
            Spacer(Modifier.height(4.dp))
            BubbleMeta(time, isMine, textColor)
        }
    }
}

// Video bubble
@Composable
private fun VideoBubble(msg: ChatMessage, isMine: Boolean, time: String) {
    val bgColor = if (isMine) BubbleMine else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) BubbleMineText else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(
        topStart = 16.dp, topEnd = 16.dp,
        bottomStart = if (isMine) 16.dp else 4.dp,
        bottomEnd = if (isMine) 4.dp else 16.dp,
    )
    Surface(color = bgColor, shape = shape, tonalElevation = 0.dp, modifier = Modifier.widthIn(max = 260.dp)) {
        Column(Modifier.padding(5.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.PlayCircle, contentDescription = "Play video", tint = Color.White, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text("Video", style = MaterialTheme.typography.labelSmall, color = textColor, modifier = Modifier.weight(1f))
                BubbleMeta(time, isMine, textColor)
            }
        }
    }
}

// File bubble (not in ChatMessageType but useful for reference)
@Composable
fun FileBubble(
    fileName: String,
    fileSize: String,
    isMine: Boolean,
    time: String,
    icon: ImageVector = Icons.Default.InsertDriveFile,
) {
    val bgColor = if (isMine) BubbleMine else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) BubbleMineText else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(
        topStart = 18.dp, topEnd = 18.dp,
        bottomStart = if (isMine) 18.dp else 4.dp,
        bottomEnd = if (isMine) 4.dp else 18.dp,
    )
    Surface(color = bgColor, shape = shape, tonalElevation = 0.dp, modifier = Modifier.widthIn(max = 280.dp)) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(textColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(fileName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = textColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(fileSize, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
                }
            }
            Spacer(Modifier.height(4.dp))
            BubbleMeta(time, isMine, textColor)
        }
    }
}

// Timestamp + tick row inside bubbles
@Composable
private fun BubbleMeta(time: String, isMine: Boolean, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(time, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.75f), fontSize = 10.sp)
        if (isMine) {
            Spacer(Modifier.width(3.dp))
            Icon(Icons.Default.DoneAll, contentDescription = null, tint = textColor.copy(alpha = 0.75f), modifier = Modifier.size(12.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Input bar
// ─────────────────────────────────────────────

@Composable
private fun ChatInputBar(
    draft: String,
    onDraftChange: (String) -> Unit,
    attachedUri: Uri?,
    attachedName: String,
    onRemoveAttachment: () -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit,
    onAttachFile: () -> Unit,
    onAttachVideo: () -> Unit,
    onRecord: () -> Unit,
    onTypingStatus: (String) -> Unit,
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            // Attachment shortcut strip
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                AttachChip(label = "Photo", icon = Icons.Default.Photo, onClick = onAttachImage)
                AttachChip(label = "File", icon = Icons.Default.AttachFile, onClick = onAttachFile)
                AttachChip(label = "Video", icon = Icons.Default.Videocam, onClick = onAttachVideo)
            }

            // Attachment preview row
            if (attachedUri != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AsyncImage(
                            model = attachedUri,
                            contentDescription = "Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp)),
                        )
                        Text(
                            attachedName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        IconButton(onClick = onRemoveAttachment, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Text field + action button row
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp),
                    placeholder = {
                        Text("Type a message…", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    },
                    shape = RoundedCornerShape(22.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandTeal,
                        cursorColor = BrandTeal,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )

                val hasDraft = draft.isNotBlank() || attachedUri != null

                if (!hasDraft) {
                    // Mic button
                    IconButton(
                        onClick = onRecord,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Record", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    // Send button
                    FilledIconButton(
                        onClick = onSend,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = BrandTeal),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
