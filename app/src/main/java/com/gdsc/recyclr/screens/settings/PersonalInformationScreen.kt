package com.gdsc.recyclr.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.RecyclrScrollScreen
import com.gdsc.recyclr.components.settings.AccountStatsGrid
import com.gdsc.recyclr.components.settings.EditDisplayNameDialog
import com.gdsc.recyclr.components.settings.PersonalInfoFieldRow
import com.gdsc.recyclr.components.settings.ProfileHeader
import com.gdsc.recyclr.domain.model.Response.Failure
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.screens.profile.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun PersonalInformationScreen(onBack: () -> Unit) {
    val activity = LocalContext.current as ComponentActivity
    val profileVm: ProfileViewModel = hiltViewModel(activity)
    val user = profileVm.currentUser
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showEditNameDialog by remember { mutableStateOf(false) }

    val impact = (profileVm.impactResponse as? Success)?.data
    val isUpdatingName = profileVm.updateDisplayNameResponse is Loading

    LaunchedEffect(profileVm.updateDisplayNameResponse) {
        when (val response = profileVm.updateDisplayNameResponse) {
            is Success -> if (response.data == true) {
                showEditNameDialog = false
                snackbarHostState.showSnackbar(context.getString(R.string.personal_info_name_updated))
            }
            is Failure -> snackbarHostState.showSnackbar(
                response.e.message ?: context.getString(R.string.personal_info_update_error),
            )
            else -> Unit
        }
    }

    if (showEditNameDialog && user != null) {
        EditDisplayNameDialog(
            currentName = user.displayName.orEmpty(),
            onDismiss = { if (!isUpdatingName) showEditNameDialog = false },
            onSave = { profileVm.updateDisplayName(it) },
            isLoading = isUpdatingName,
        )
    }

    RecyclrScrollScreen(
        title = stringResource(R.string.personal_info_title),
        onBack = onBack,
        verticalSpacing = 20.dp,
        snackbarHostState = snackbarHostState,
    ) {
        if (user == null) {
            Text(
                text = stringResource(R.string.personal_info_guest),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            val displayName = user.displayName.orEmpty().ifBlank {
                stringResource(R.string.personal_info_default_name)
            }
            val email = user.email.orEmpty().ifBlank { "—" }
            val memberSinceText = profileVm.getMemberSinceFormatted()?.let { date ->
                stringResource(R.string.personal_info_member_since, date)
            }

            ProfileHeader(
                displayName = displayName,
                email = email,
                memberSince = memberSinceText,
                onEditClick = { showEditNameDialog = true },
            )

            if (impact != null) {
                AccountStatsGrid(
                    totalScans = impact.totalScans,
                    points = impact.pointsBalance,
                    activeDays = profileVm.accountActiveDays,
                    co2SavedKg = impact.co2SavedKg,
                )
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PersonalInfoFieldRow(
                        label = stringResource(R.string.personal_info_email),
                        value = email,
                        onCopyClick = {
                            copyToClipboard(context, email)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.personal_info_copy_email_done),
                                )
                            }
                        },
                    )
                    HorizontalDivider()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (profileVm.isEmailVerified) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                        )
                        Text(
                            text = if (profileVm.isEmailVerified) {
                                stringResource(R.string.personal_info_verified)
                            } else {
                                stringResource(R.string.personal_info_not_verified)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    if (text.isBlank() || text == "—") return
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("email", text))
}
