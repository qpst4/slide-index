package com.slideindex.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slideindex.app.R
import com.slideindex.app.SlideIndexApp
import com.slideindex.app.data.AppInfo
import com.slideindex.app.settings.AppSettings
import com.slideindex.app.util.PinyinHelper
import com.slideindex.app.util.toSafeImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenAppsScreen(
    settings: AppSettings,
    onBack: () -> Unit,
    onHideApp: (String) -> Unit,
    onUnhideApp: (String) -> Unit,
) {
    val context = LocalContext.current
    val appRepository = remember { (context.applicationContext as SlideIndexApp).appRepository }
    var allApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        allApps = appRepository.loadApps(force = true)
        isLoading = false
    }

    val hiddenPackages = settings.hiddenAppPackages
    val appsByPackage = remember(allApps) { allApps.associateBy { it.packageName } }
    val hiddenEntries = remember(hiddenPackages, allApps) {
        hiddenPackages.sorted().map { packageName ->
            appsByPackage[packageName]?.let { HiddenAppEntry.Installed(it) }
                ?: HiddenAppEntry.Missing(packageName)
        }
    }
    val addableApps = remember(allApps, hiddenPackages, searchQuery) {
        val query = searchQuery.trim().lowercase()
        allApps
            .filter { it.packageName !in hiddenPackages }
            .filter { app ->
                if (query.isEmpty()) return@filter true
                app.label.lowercase().contains(query) ||
                    app.packageName.lowercase().contains(query) ||
                    PinyinHelper.sortKey(app.label).contains(query)
            }
            .sortedBy { PinyinHelper.sortKey(it.label) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hidden_apps_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.hidden_apps_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))

            SettingsSectionTitle(stringResource(R.string.hidden_apps_section_hidden))
            Spacer(modifier = Modifier.height(8.dp))

            if (hiddenEntries.isEmpty()) {
                Text(
                    text = stringResource(R.string.hidden_apps_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(hiddenEntries, key = { it.packageName }) { entry ->
                        HiddenAppRow(
                            entry = entry,
                            actionIcon = Icons.Default.Close,
                            actionDescription = stringResource(R.string.hidden_apps_unhide),
                            onAction = { onUnhideApp(entry.packageName) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SettingsSectionTitle(stringResource(R.string.hidden_apps_section_add))
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(R.string.loading))
                    }
                }
                addableApps.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) {
                                stringResource(R.string.hidden_apps_all_hidden)
                            } else {
                                stringResource(R.string.no_apps)
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(addableApps, key = { it.packageName }) { app ->
                            HiddenAppRow(
                                entry = HiddenAppEntry.Installed(app),
                                actionIcon = Icons.Default.Add,
                                actionDescription = stringResource(R.string.hidden_apps_hide),
                                onAction = { onHideApp(app.packageName) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HiddenAppsEntryCard(
    hiddenCount: Int,
    onClick: () -> Unit,
) {
    val subtitle = if (hiddenCount > 0) {
        stringResource(R.string.hidden_apps_entry_count, hiddenCount)
    } else {
        stringResource(R.string.hidden_apps_entry_desc)
    }
    SettingNavigationRow(
        icon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) },
        title = stringResource(R.string.hidden_apps_entry_title),
        subtitle = subtitle,
        onClick = onClick,
    )
}

@Composable
private fun HiddenAppRow(
    entry: HiddenAppEntry,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    actionDescription: String,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onAction)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (entry) {
            is HiddenAppEntry.Installed -> {
                val bitmap = remember(entry.app.packageName) {
                    entry.app.icon.toSafeImageBitmap(96)
                }
                Image(
                    bitmap = bitmap,
                    contentDescription = entry.app.label,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.app.label,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                    )
                    Text(
                        text = entry.app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
            is HiddenAppEntry.Missing -> {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.packageName,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                    )
                    Text(
                        text = stringResource(R.string.hidden_apps_uninstalled),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        IconButton(onClick = onAction) {
            Icon(
                imageVector = actionIcon,
                contentDescription = actionDescription,
            )
        }
    }
}

private sealed class HiddenAppEntry {
    abstract val packageName: String

    data class Installed(val app: AppInfo) : HiddenAppEntry() {
        override val packageName: String = app.packageName
    }

    data class Missing(override val packageName: String) : HiddenAppEntry()
}
