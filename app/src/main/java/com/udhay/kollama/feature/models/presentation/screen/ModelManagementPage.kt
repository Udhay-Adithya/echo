package com.udhay.kollama.feature.models.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.kollama.R
import com.udhay.kollama.core.ui.common.AppTextField
import com.udhay.kollama.core.utils.formatDate
import com.udhay.kollama.core.utils.formatFileSize
import com.udhay.kollama.feature.chat.domain.model.OllamaModel
import com.udhay.kollama.feature.models.domain.model.RunningModel
import com.udhay.kollama.feature.models.presentation.components.ModelDetailsSheet
import com.udhay.kollama.feature.models.presentation.state.TransferUiState
import com.udhay.kollama.feature.models.presentation.viewmodel.ModelManagementViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementPage(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelManagementViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val detail by viewModel.detail.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var pullText by remember { mutableStateOf("") }
    var copyTarget by remember { mutableStateOf<OllamaModel?>(null) }
    var deleteTarget by remember { mutableStateOf<OllamaModel?>(null) }

    LaunchedEffect(state.message, state.error) {
        val text = state.error ?: state.message
        if (text != null) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Models", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_ios_24px),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            painter = painterResource(R.drawable.refresh_24px),
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PullBar(
                    value = pullText,
                    onValueChange = { pullText = it },
                    enabled = state.transfer == null,
                    onPull = {
                        viewModel.pull(pullText)
                        pullText = ""
                    }
                )
            }

            state.transfer?.let { transfer ->
                item { TransferCard(transfer) }
            }

            if (state.running.isNotEmpty()) {
                item { SectionHeader("Running now") }
                items(state.running, key = { "run-${it.name}" }) { RunningModelCard(it) }
            }

            item { SectionHeader("Installed models") }

            if (state.isLoading && state.models.isEmpty()) {
                item {
                    Text(
                        "Loading…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (state.models.isEmpty()) {
                item {
                    Text(
                        state.error ?: "No models installed. Pull one above to get started.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(state.models, key = { "model-${it.name}" }) { model ->
                InstalledModelItem(
                    model = model,
                    onDetails = { model.name?.let { viewModel.showDetails(it) } },
                    onCopy = { copyTarget = model },
                    onPush = { model.name?.let { viewModel.push(it) } },
                    onDelete = { deleteTarget = model }
                )
            }
        }
    }

    detail?.let {
        ModelDetailsSheet(detail = it, onDismiss = { viewModel.dismissDetails() })
    }

    copyTarget?.let { target ->
        CopyModelDialog(
            source = target.name.orEmpty(),
            onConfirm = { destination ->
                viewModel.copy(target.name.orEmpty(), destination)
                copyTarget = null
            },
            onDismiss = { copyTarget = null }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete model") },
            text = { Text("Remove ${target.name} from the server? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    target.name?.let { viewModel.delete(it) }
                    deleteTarget = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun PullBar(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onPull: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "Pull a model, e.g. llama3.2",
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        FilledIconButton(onClick = onPull, enabled = enabled && value.isNotBlank()) {
            Icon(
                painter = painterResource(R.drawable.download_24px),
                contentDescription = "Pull model"
            )
        }
    }
}

@Composable
private fun TransferCard(transfer: TransferUiState) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${transfer.kind.name.lowercase().replaceFirstChar { it.uppercase() }}ing ${transfer.model}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = transfer.progress.status ?: "working…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            val fraction = transfer.progress.fraction
            if (fraction != null) {
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${formatFileSize(transfer.progress.completed.takeIf { it > 0 })} / ${formatFileSize(transfer.progress.total)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun RunningModelCard(model: RunningModel) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.lightbulb_2_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(model.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val info = buildList {
                    model.sizeVram?.let { add("VRAM ${formatFileSize(it)}") }
                    model.contextLength?.let { add("ctx $it") }
                }.joinToString(" · ")
                if (info.isNotEmpty()) {
                    Text(
                        info,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                model.expiresAt?.let {
                    Text(
                        "Unloads at ${formatDate(it)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InstalledModelItem(
    model: OllamaModel,
    onDetails: () -> Unit,
    onCopy: () -> Unit,
    onPush: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(model.name ?: "unknown", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = { Text(formatFileSize(model.size)) },
        trailingContent = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    painter = painterResource(R.drawable.more_vert_24px),
                    contentDescription = "Actions"
                )
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Details") }, onClick = {
                        menuExpanded = false; onDetails()
                    })
                    DropdownMenuItem(text = { Text("Copy") }, onClick = {
                        menuExpanded = false; onCopy()
                    })
                    DropdownMenuItem(text = { Text("Push") }, onClick = {
                        menuExpanded = false; onPush()
                    })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = {
                        menuExpanded = false; onDelete()
                    })
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    )
}

@Composable
private fun CopyModelDialog(
    source: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var destination by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Copy model") },
        text = {
            Column {
                Text("Duplicate $source under a new name.")
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    placeholder = "new-model-name",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(destination) }, enabled = destination.isNotBlank()) {
                Text("Copy")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
