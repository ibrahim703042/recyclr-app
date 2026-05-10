package com.gdsc.recyclr.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.ui.theme.Gray_color

@Composable
fun ProfileContent(
    padding: PaddingValues,
    user: User?,
    impactResponse: Response<UserImpact>
) {
    val displayName = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.phoneNumber?.takeIf { it.isNotBlank() }
        ?: "Invité"
    val subtitle = when {
        user == null -> "Connectez-vous pour synchroniser vos points."
        user.email.isNullOrBlank() && !user.phoneNumber.isNullOrBlank() -> "Compte téléphone"
        else -> user.authProvidersSummary ?: ""
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray_color)
            .padding(padding)
    ) {
        val maxCard = minOf(maxWidth, 560.dp)
        Column(
            modifier = Modifier
                .widthIn(max = maxCard)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!user?.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = user!!.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(88.dp),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle.isNotBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            if (user?.isEmailVerified == true) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "E-mail vérifié",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.primary
                                )
                            }
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Coordonnées", fontWeight = FontWeight.SemiBold)
                    Divider()
                    ProfileRow(label = "E-mail", value = user?.email ?: "—")
                    ProfileRow(label = "Téléphone", value = user?.phoneNumber ?: "—")
                    ProfileRow(label = "UID", value = user?.uid ?: "—")
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Impact & récompenses", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    when (impactResponse) {
                        is Response.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        is Response.Failure -> {
                            Text(
                                text = "Impossible de charger l’impact (${impactResponse.e.message ?: "erreur"})",
                                color = MaterialTheme.colors.error
                            )
                        }
                        is Response.Success -> {
                            val imp = impactResponse.data
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Points",
                                    value = "${imp?.pointsBalance ?: 0}"
                                )
                                StatPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Scans",
                                    value = "${imp?.totalScans ?: 0}"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatPill(
                                    modifier = Modifier.weight(1f),
                                    title = "CO₂ évité",
                                    value = String.format("%.2f kg", imp?.co2SavedKg ?: 0f)
                                )
                                StatPill(
                                    modifier = Modifier.weight(1f),
                                    title = "Déchets",
                                    value = String.format("%.2f kg", imp?.wasteDivertedKg ?: 0f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            StatPill(
                                modifier = Modifier.fillMaxWidth(),
                                title = "Équivalent arbres",
                                value = "${imp?.treesEquivalent ?: 0}"
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Text(text = value, style = MaterialTheme.typography.body1, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun StatPill(modifier: Modifier = Modifier, title: String, value: String) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.surface,
        elevation = 1.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
