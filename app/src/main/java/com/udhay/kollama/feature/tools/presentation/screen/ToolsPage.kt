package com.udhay.kollama.feature.tools.presentation.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.kollama.R
import com.udhay.kollama.core.ui.common.AppTextField
import com.udhay.kollama.feature.tools.domain.model.ToolDefinition
import com.udhay.kollama.feature.tools.presentation.viewmodel.ToolsViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

private const val DEFAULT_PARAMETERS = """{
  "type": "object",
  "properties": {
    "location": { "type": "string", "description": "City and state, e.g. San Francisco, CA" }
  },
  "required": ["location"]
}"""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsPage(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ToolsViewModel = koinViewModel()
) {
    val tools by viewModel.tools.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<ToolDefinition?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tools", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_ios_24px),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editing = null
                showEditor = true
            }) {
                Icon(painter = painterResource(R.drawable.add_24px), contentDescription = "New tool")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Tools let capable models call functions you define. The model returns " +
                            "a call with arguments; you provide the result to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (tools.isEmpty()) {
                item {
                    Text(
                        "No tools yet. Tap + to create one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(tools, key = { it.id }) { tool ->
                ListItem(
                    headlineContent = {
                        Text(tool.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    supportingContent = {
                        if (tool.description.isNotBlank()) {
                            Text(tool.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = tool.enabled,
                                onCheckedChange = { viewModel.setEnabled(tool.id, it) }
                            )
                            IconButton(onClick = { viewModel.delete(tool.id) }) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24px),
                                    contentDescription = "Delete tool"
                                )
                            }
                        }
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier.clickable { editing = tool; showEditor = true }
                )
            }
        }
    }

    if (showEditor) {
        ToolEditorSheet(
            initial = editing,
            onSave = { viewModel.save(it); showEditor = false },
            onDismiss = { showEditor = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolEditorSheet(
    initial: ToolDefinition?,
    onSave: (ToolDefinition) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var parameters by remember { mutableStateOf(initial?.parameters ?: DEFAULT_PARAMETERS) }

    val jsonValid = remember(parameters) {
        runCatching { Json.parseToJsonElement(parameters) }.isSuccess
    }
    val canSave = name.isNotBlank() && jsonValid

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (initial == null) "New tool" else "Edit tool",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel("Function name")
            AppTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "get_current_weather",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel("Description")
            AppTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "What the function does",
                singleLine = false,
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel("Parameters (JSON Schema)")
            AppTextField(
                value = parameters,
                onValueChange = { parameters = it },
                singleLine = false,
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
            if (!jsonValid) {
                Text(
                    text = "Parameters must be valid JSON.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    enabled = canSave,
                    onClick = {
                        onSave(
                            ToolDefinition(
                                id = initial?.id ?: UUID.randomUUID().toString(),
                                name = name.trim(),
                                description = description.trim(),
                                parameters = parameters.trim(),
                                enabled = initial?.enabled ?: true,
                                createdAt = initial?.createdAt ?: System.currentTimeMillis()
                            )
                        )
                    }
                ) { Text("Save") }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
