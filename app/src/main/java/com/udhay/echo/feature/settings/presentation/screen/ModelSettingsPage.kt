package com.udhay.echo.feature.settings.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.R
import com.udhay.echo.core.ui.common.AppTextField
import com.udhay.echo.feature.chat.presentation.components.ModelSelectorBottomSheet
import com.udhay.echo.feature.settings.presentation.components.SettingListItemHeader
import com.udhay.echo.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSettingsPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: UserSettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var temperature by remember(settings.temperature) {
        mutableStateOf(settings.temperature?.toString() ?: "")
    }
    var topK by remember(settings.topK) { mutableStateOf(settings.topK?.toString() ?: "") }
    var topP by remember(settings.topP) { mutableStateOf(settings.topP?.toString() ?: "") }
    var numCtx by remember(settings.numCtx) { mutableStateOf(settings.numCtx?.toString() ?: "") }
    var keepAlive by remember(settings.keepAlive) { mutableStateOf(settings.keepAlive ?: "") }

    val hasChanges =
        temperature != (settings.temperature?.toString() ?: "") ||
                topK != (settings.topK?.toString() ?: "") ||
                topP != (settings.topP?.toString() ?: "") ||
                numCtx != (settings.numCtx?.toString() ?: "") ||
                keepAlive != (settings.keepAlive ?: "")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Model Settings", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_ios_24px),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.save(
                                settings.copy(
                                    temperature = temperature.toFloatOrNull(),
                                    topK = topK.toIntOrNull(),
                                    topP = topP.toFloatOrNull(),
                                    numCtx = numCtx.toIntOrNull(),
                                    keepAlive = keepAlive.ifBlank { null }
                                )
                            )
                        },
                        enabled = hasChanges
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingListItemHeader(heading = "Default Model")
            ModelSelectorBottomSheet()

            Spacer(Modifier.height(8.dp))
            SettingListItemHeader(heading = "Reasoning")
            ListItem(
                headlineContent = { Text("Thinking") },
                supportingContent = { Text("Ask the model to reason before answering") },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.lightbulb_2_24px),
                        contentDescription = null
                    )
                },
                trailingContent = {
                    Switch(
                        checked = settings.thinkingEnabled,
                        onCheckedChange = { viewModel.save(settings.copy(thinkingEnabled = it)) }
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            Spacer(Modifier.height(8.dp))
            SettingListItemHeader(heading = "Generation")
            Text(
                text = "Leave a field empty to use the server default.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LabeledField("Temperature", temperature, "0.8") { temperature = it }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    LabeledField("Top K", topK, "40") { topK = it }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    LabeledField("Top P", topP, "0.9") { topP = it }
                }
            }
            Spacer(Modifier.height(16.dp))
            LabeledField("Context length (num_ctx)", numCtx, "4096") { numCtx = it }
            Spacer(Modifier.height(16.dp))
            LabeledField("Keep alive", keepAlive, "5m") { keepAlive = it }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = Modifier.fillMaxWidth()
    )
}
