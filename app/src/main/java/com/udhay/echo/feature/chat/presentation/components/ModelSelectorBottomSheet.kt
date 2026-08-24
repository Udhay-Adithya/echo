package com.udhay.echo.feature.chat.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.R
import com.udhay.echo.core.ui.common.ErrorView
import com.udhay.echo.core.ui.common.Loader
import com.udhay.echo.core.utils.formatDate
import com.udhay.echo.core.utils.formatFileSize
import com.udhay.echo.core.utils.prettyPrintJson
import com.udhay.echo.feature.chat.presentation.state.ModelsUiState
import com.udhay.echo.feature.chat.presentation.viewmodel.ModelsViewModel
import com.udhay.echo.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectorBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: ModelsViewModel = koinViewModel(),
    settingsViewModel: UserSettingsViewModel = koinViewModel()
) {
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val runningModels by viewModel.runningModels.collectAsStateWithLifecycle()

    var showSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    TextButton(
        onClick = { showSheet = true },
        colors = ButtonDefaults.textButtonColors()
            .copy(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Text(text = settings.selectedModel?.name ?: "Model")
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Models",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = { viewModel.getModels() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.refresh_24px),
                        contentDescription = "Refresh"
                    )
                }
            }
            when (val currentState = state) {
                is ModelsUiState.Loading -> {
                    Loader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                            .heightIn(min = 400.dp, max = 400.dp),
                    )
                }

                is ModelsUiState.Error -> {
                    ErrorView(
                        message = currentState.message,
                        onRetry = { viewModel.getModels() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                            .heightIn(min = 400.dp, max = 400.dp),
                    )
                }

                is ModelsUiState.Success -> {
                    val models = currentState.models
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                            .heightIn(min = 400.dp, max = 400.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(models) { model ->
                            val isRunning = model.name in runningModels || model.model in runningModels
                            ListItem(
                                headlineContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = model.name ?: "N/A")
                                        if (isRunning) {
                                            Spacer(Modifier.width(8.dp))
                                            ReadyChip()
                                        }
                                    }
                                },
                                supportingContent = {
                                    Text("Last Modified: ${formatDate(model.modifiedAt)}")
                                },
                                overlineContent = {
                                    Text(formatFileSize(model.size))
                                },
                                trailingContent = {
                                    ModelDetailToolTip(
                                        richTooltipText = prettyPrintJson(model.details)
                                    )
                                },
                                colors = ListItemDefaults.colors()
                                    .copy(MaterialTheme.colorScheme.surfaceContainerHigh),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.onModelChosen(model) { resolved ->
                                            settingsViewModel.save(settings.copy(selectedModel = resolved))
                                        }
                                        showSheet = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadyChip() {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = "Ready",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModelSelectorBottomSheetPreview() {
    ModelSelectorBottomSheet()
}
