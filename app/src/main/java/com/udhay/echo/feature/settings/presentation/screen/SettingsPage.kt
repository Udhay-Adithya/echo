package com.udhay.echo.feature.settings.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.R
import com.udhay.echo.core.ui.theme.EchoTheme
import com.udhay.echo.feature.settings.presentation.components.SettingListItemHeader
import com.udhay.echo.feature.settings.presentation.components.SettingsListItem
import com.udhay.echo.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

private data class FontSizeOption(val label: String, val scale: Float)

private val fontSizeOptions = listOf(
    FontSizeOption("Small", 0.85f),
    FontSizeOption("Default", 1.0f),
    FontSizeOption("Large", 1.15f),
    FontSizeOption("Extra Large", 1.3f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToPersonalization: () -> Unit,
    onNavigateToConnectionSettings: () -> Unit,
    onNavigateToModelSettings: () -> Unit = {},
    onNavigateToModelManagement: () -> Unit = {},
    onNavigateToTools: () -> Unit = {},
    viewModel: UserSettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showFontDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateBack() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_ios_24px),
                            contentDescription = "Back"
                        )
                    }

                }

            )
        }

    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingListItemHeader(heading = "General")

            SettingsListItem(
                onTap = onNavigateToPersonalization,
                title = "Personalization",
                leadingIcon = painterResource(R.drawable.person_24px),
                isFirst = true,
            )
            SettingsListItem(
                onTap = {},
                title = "Data Control",
                leadingIcon = painterResource(R.drawable.text_snippet_24px),
            )
            SettingsListItem(
                onTap = onNavigateToConnectionSettings,
                title = "Connection Settings",
                leadingIcon = painterResource(R.drawable.settings_24px),
            )
            SettingsListItem(
                onTap = onNavigateToModelSettings,
                title = "Model Settings",
                leadingIcon = painterResource(R.drawable.lightbulb_2_24px),
            )
            SettingsListItem(
                onTap = onNavigateToModelManagement,
                title = "Manage Models",
                leadingIcon = painterResource(R.drawable.download_24px),
            )
            SettingsListItem(
                onTap = onNavigateToTools,
                title = "Tools",
                leadingIcon = painterResource(R.drawable.web_24px),
                isLast = true,
            )

            SettingListItemHeader(heading = "App")

            SettingsListItem(
                onTap = {},
                title = "Language",
                leadingIcon = painterResource(R.drawable.language_24px),
                isFirst = true,
            )
            SettingsListItem(
                onTap = {},
                title = "Dark Mode",
                leadingIcon = painterResource(R.drawable.moon_stars_24px),
                trailingIcon = {
                    Switch(
                        checked = settings.darkModeEnabled,
                        onCheckedChange = { isChecked ->
                            viewModel.save(settings.copy(darkModeEnabled = isChecked))
                        })
                }
            )
            SettingsListItem(
                onTap = {},
                title = "Amoled Palette",
                leadingIcon = painterResource(R.drawable.web_24px),
                trailingIcon = {
                    Switch(
                        checked = settings.amoledPaletteEnabled,
                        onCheckedChange = { isChecked ->
                            viewModel.save(settings.copy(amoledPaletteEnabled = isChecked))
                        }
                    )
                }
            )
            SettingsListItem(
                onTap = { showFontDialog = true },
                title = "Font Size",
                leadingIcon = painterResource(R.drawable.format_size_24px),
                isLast = true,
                trailingIcon = {
                    Text(
                        text = fontSizeOptions
                            .firstOrNull { it.scale == settings.fontScale }?.label ?: "Custom",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            SettingListItemHeader(heading = "About")
            SettingsListItem(
                onTap = {},
                title = "Check for updates",
                leadingIcon = painterResource(R.drawable.info_24px),
                isFirst = true,
            )
            SettingsListItem(
                onTap = {},
                title = "Help & Feedback",
                leadingIcon = painterResource(R.drawable.help_24px),
                isLast = true,
            )
        }
    }

    if (showFontDialog) {
        FontSizeDialog(
            current = settings.fontScale,
            onSelect = {
                viewModel.save(settings.copy(fontScale = it))
                showFontDialog = false
            },
            onDismiss = { showFontDialog = false }
        )
    }
}

@Composable
private fun FontSizeDialog(
    current: Float,
    onSelect: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Font size") },
        text = {
            Column {
                fontSizeOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = option.scale == current,
                                onClick = { onSelect(option.scale) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option.scale == current,
                            onClick = { onSelect(option.scale) }
                        )
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsPagePreview() {
    EchoTheme(
        darkTheme = false
    ) {
        SettingsPage(
            onNavigateBack = {},
            onNavigateToPersonalization = {},
            onNavigateToConnectionSettings = {}
        )
    }
}
