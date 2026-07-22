package com.udhay.kollama.feature.settings.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.kollama.R
import com.udhay.kollama.core.ui.theme.KollamaTheme
import com.udhay.kollama.feature.settings.presentation.components.SettingListItemHeader
import com.udhay.kollama.feature.settings.presentation.components.SettingsListItem
import com.udhay.kollama.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToPersonalization: () -> Unit,
    onNavigateToConnectionSettings: () -> Unit,
    onNavigateToModelSettings: () -> Unit = {},
    onNavigateToModelManagement: () -> Unit = {},
    viewModel: UserSettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

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
                onTap = {},
                title = "Font Size",
                leadingIcon = painterResource(R.drawable.format_size_24px),
                isLast = true,
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
}

@Preview(showBackground = true)
@Composable
private fun SettingsPagePreview() {
    KollamaTheme(
        darkTheme = false
    ) {
        SettingsPage(
            onNavigateBack = {},
            onNavigateToPersonalization = {},
            onNavigateToConnectionSettings = {}
        )
    }
}
