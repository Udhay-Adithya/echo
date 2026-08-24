package com.udhay.echo.feature.settings.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.R
import com.udhay.echo.core.ui.common.AppTextField
import com.udhay.echo.feature.chat.presentation.components.ModelSelectorBottomSheet
import com.udhay.echo.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizationPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: UserSettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var username by remember(settings.username) {
        mutableStateOf(settings.username)
    }

    var occupation by remember(settings.occupation) {
        mutableStateOf(settings.occupation)
    }

    var preferences by remember(settings.personalPreferences) {
        mutableStateOf(settings.personalPreferences)
    }

    val hasChanges =
        username != settings.username ||
                occupation != settings.occupation ||
                preferences != settings.personalPreferences

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Personalization",
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
                            viewModel.save(
                                settings.copy(
                                    username = username,
                                    occupation = occupation,
                                    personalPreferences = preferences
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Name",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AppTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Occupation",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AppTextField(
                value = occupation,
                onValueChange = { occupation = it },
                placeholder = "Occupation",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Preferences",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AppTextField(
                value = preferences,
                onValueChange = { preferences = it },
                placeholder = "e.g., I prefer concise answers",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 5,
                borderRadius = 18.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Default Model",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ModelSelectorBottomSheet()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
