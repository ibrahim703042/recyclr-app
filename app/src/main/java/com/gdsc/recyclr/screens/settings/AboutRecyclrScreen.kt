package com.gdsc.recyclr.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.RecyclrScrollScreen
import com.gdsc.recyclr.components.settings.AppInfoCard
import com.gdsc.recyclr.components.settings.WhatsNewSection
import com.gdsc.recyclr.util.readAppVersionInfo

@Composable
fun AboutRecyclrScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionInfo = remember(context) { readAppVersionInfo(context) }
    val whatsNewItems = listOf(
        stringResource(R.string.about_whats_new_item_wallet),
        stringResource(R.string.about_whats_new_item_settings),
        stringResource(R.string.about_whats_new_item_engagement),
    )

    RecyclrScrollScreen(
        title = stringResource(R.string.about_title),
        onBack = onBack,
    ) {
        AppInfoCard(
            version = versionInfo.versionName,
            buildNumber = versionInfo.buildNumber,
        )
        WhatsNewSection(items = whatsNewItems)
    }
}
