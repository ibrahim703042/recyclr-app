package com.gdsc.recyclr.screens.support

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.BasicTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpWebViewScreen(onBack: () -> Unit) {
    val url = stringResource(R.string.help_center_url)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            BasicTopBar(
                title = stringResource(R.string.help_center_title),
                onBack = onBack,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        AndroidView(
            factory = {
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(url)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}
