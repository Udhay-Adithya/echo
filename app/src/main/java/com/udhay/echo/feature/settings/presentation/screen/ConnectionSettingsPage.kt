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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.R
import com.udhay.echo.core.ui.common.AppTextField
import com.udhay.echo.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionSettingsPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: UserSettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var host by remember(settings.serverHost) {
        mutableStateOf(settings.serverHost)
    }
    // Header Input Section
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }

    val hasChanges = host != settings.serverHost

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Connection Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
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
                            viewModel.save(settings.copy(serverHost = host))
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Host URL",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AppTextField(
                value = host,
                onValueChange = { host = it },
                placeholder = "http://192.168.1.100:11434",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Custom Headers",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextField(
                    value = newKey,
                    onValueChange = { newKey = it },
                    modifier = Modifier.weight(1f),
                    placeholder = "Key"
                )
                Spacer(modifier = Modifier.width(8.dp))

                AppTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    modifier = Modifier.weight(1f),
                    placeholder = "Value"
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newKey.isNotBlank()) {
                            val updatedHeaders = settings.serverHeaders.toMutableMap()
                            updatedHeaders[newKey] = newValue
                            viewModel.save(settings.copy(serverHeaders = updatedHeaders))
                            newKey = ""
                            newValue = ""
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24px),
                        contentDescription = "Add Header"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (settings.serverHeaders.isNotEmpty()) {
                Text(
                    text = "Active Headers",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }


            // Display Existing Headers
            settings.serverHeaders.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = key,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            val updatedHeaders = settings.serverHeaders.toMutableMap()
                            updatedHeaders.remove(key)
                            viewModel.save(settings.copy(serverHeaders = updatedHeaders))
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = "Remove Header"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
