package com.gdsc.recyclr.screens.notifications

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.BasicTopBar
import java.text.DateFormat
import java.util.Date

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val items by viewModel.notifications.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            BasicTopBar(title = stringResource(R.string.notifications_page_title), onBack = onBack)
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            items(items, key = { it.id }) { n ->
                ListItem(
                    headlineContent = {
                        Text(n.title, fontWeight = if (n.read) FontWeight.Normal else FontWeight.Bold)
                    },
                    supportingContent = { Text(n.body, style = MaterialTheme.typography.bodyMedium) },
                    trailingContent = {
                        Text(
                            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(Date(n.createdAtMillis)),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                HorizontalDivider()
            }
        }
    }
}
