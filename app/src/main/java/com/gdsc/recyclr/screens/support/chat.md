package com.gdsc.recyclr.screens.support

const val SUPPORT_CHAT_THREAD_ID = "support"

package com.gdsc.recyclr.screens.support

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.ChatMessage
import com.gdsc.recyclr.domain.model.ChatMessageType
import java.text.DateFormat
import java.util.Date

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

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.sendMediaMessage(it, "image/jpeg", ChatMessageType.IMAGE) }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.markSupportThreadSeenOnLeave() }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.support_chat_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        val otherStatus = typingStatuses.values.firstOrNull { it != "none" }
                        if (otherStatus != null) {
                            Text(
                                text = when (otherStatus) {
                                    "typing" -> "typing..."
                                    "recording" -> "recording audio..."
                                    else -> "Online"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Text(
                                stringResource(R.string.support_chat_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        IconButton(onClick = { imagePicker.launch("image/*") }) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach")
                        }
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { 
                                draft = it
                                viewModel.setTypingStatus(if (it.isNotEmpty()) "typing" else "none")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp, max = 120.dp),
                            placeholder = { Text(stringResource(R.string.support_chat_hint)) },
                            shape = RoundedCornerShape(22.dp),
                            maxLines = 4,
                        )
                        if (draft.isBlank()) {
                            IconButton(onClick = { /* TODO: Start Audio Recording */ }) {
                                Icon(Icons.Default.Mic, contentDescription = "Record")
                            }
                        } else {
                            FilledIconButton(
                                onClick = {
                                    viewModel.sendUserMessage(draft)
                                    draft = ""
                                },
                                modifier = Modifier.size(48.dp),
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = stringResource(R.string.support_chat_send),
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column {
                        Text(
                            stringResource(R.string.support_chat_team),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            stringResource(R.string.support_chat_online),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            messages.forEach { msg ->
                ChatBubble(msg)
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val align = if (msg.fromUser) Alignment.End else Alignment.Start
    val bubbleColor = if (msg.fromUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (msg.fromUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val meta = remember(msg.sentAtMillis) {
        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(msg.sentAtMillis))
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Text(
            text = if (msg.fromUser) stringResource(R.string.support_chat_you) else stringResource(R.string.support_chat_team),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        )
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (msg.fromUser) 18.dp else 4.dp,
                bottomEnd = if (msg.fromUser) 4.dp else 18.dp,
            ),
            tonalElevation = 0.dp,
            modifier = Modifier.widthIn(max = 320.dp),
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                when (msg.type) {
                    ChatMessageType.TEXT -> {
                        Text(
                            text = msg.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                        )
                    }
                    ChatMessageType.IMAGE -> {
                        AsyncImage(
                            model = msg.fileUrl,
                            contentDescription = "Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    ChatMessageType.AUDIO -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = textColor)
                            Spacer(Modifier.width(8.dp))
                            Text("Voice message", style = MaterialTheme.typography.bodyMedium, color = textColor)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = textColor)
                        }
                    }
                    ChatMessageType.VIDEO -> {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(48.dp), tint = textColor.copy(alpha = 0.5f))
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = textColor)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.75f),
                )
            }
        }
    }
}
package com.gdsc.recyclr.screens.support

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.data.local.preferences.BadgePreferencesStore
import com.gdsc.recyclr.domain.model.ChatMessage
import com.gdsc.recyclr.domain.model.ChatMessageType
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SupportChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val badgePreferencesStore: BadgePreferencesStore,
) : ViewModel() {

    private val currentUserId = authRepository.currentUser?.uid ?: "guest"
    private val threadId = currentUserId // Support chat thread is the user's ID

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getMessages(threadId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val typingStatuses: StateFlow<Map<String, String>> = chatRepository.getTypingStatuses(threadId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun sendUserMessage(text: String) {
        if (text.isBlank() || currentUserId == "guest") return
        viewModelScope.launch {
            setTypingStatus("none")
            chatRepository.sendMessage(
                threadId,
                ChatMessage(
                    senderId = currentUserId,
                    body = text.trim(),
                    fromUser = true,
                    type = ChatMessageType.TEXT
                )
            )
        }
    }

    fun sendMediaMessage(uri: Uri, mimeType: String, type: ChatMessageType, durationMillis: Long? = null) {
        if (currentUserId == "guest") return
        viewModelScope.launch {
            setTypingStatus("none")
            val upload = chatRepository.uploadFile(threadId, uri, mimeType)
            upload.onSuccess { url ->
                chatRepository.sendMessage(
                    threadId,
                    ChatMessage(
                        senderId = currentUserId,
                        fromUser = true,
                        type = type,
                        fileUrl = url,
                        durationMillis = durationMillis
                    )
                )
            }.onFailure {
                // Log error
            }
        }
    }

    fun setTypingStatus(status: String) {
        if (currentUserId == "guest") return
        viewModelScope.launch {
            chatRepository.setTypingStatus(threadId, currentUserId, status)
        }
    }

    fun markSupportThreadSeenOnLeave() {
        viewModelScope.launch {
            badgePreferencesStore.markSupportThreadSeenNow()
            setTypingStatus("none")
        }
    }
}
