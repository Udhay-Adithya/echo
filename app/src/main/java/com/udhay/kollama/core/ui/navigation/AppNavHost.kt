package com.udhay.kollama.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.udhay.kollama.feature.chat.presentation.screen.ChatPage
import com.udhay.kollama.feature.models.presentation.screen.ModelManagementPage
import com.udhay.kollama.feature.settings.presentation.screen.ConnectionSettingsPage
import com.udhay.kollama.feature.settings.presentation.screen.ModelSettingsPage
import com.udhay.kollama.feature.settings.presentation.screen.PersonalizationPage
import com.udhay.kollama.feature.settings.presentation.screen.SettingsPage

@Composable
fun AppNavHost(
) {
    val backStack: SnapshotStateList<Routes> = remember { mutableStateListOf<Routes>(
        Routes.Chat
    ) }

    fun navigateTo(route: Routes){
        backStack.add(route)
    }
    
    fun popBack(){
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Routes.Chat -> NavEntry(key) {
                        ChatPage(
                            onOpenSettings = { navigateTo(Routes.Settings) },
                        )
                }

                is Routes.Settings -> NavEntry(key) {
                    SettingsPage(
                        onNavigateBack = { popBack() },
                        onNavigateToPersonalization = { navigateTo(Routes.Personalization) },
                        onNavigateToConnectionSettings = { navigateTo(Routes.ConnectionSettings) },
                        onNavigateToModelSettings = { navigateTo(Routes.ModelSettings) },
                        onNavigateToModelManagement = { navigateTo(Routes.ModelManagement) }
                    )
                }

                is Routes.Personalization -> NavEntry(key) {
                    PersonalizationPage(
                        onNavigateBack = { popBack() }
                    )
                }

                is Routes.ConnectionSettings -> NavEntry(key) {
                    ConnectionSettingsPage(
                        onNavigateBack = { popBack() }
                    )
                }

                is Routes.ModelSettings -> NavEntry(key) {
                    ModelSettingsPage(
                        onNavigateBack = { popBack() }
                    )
                }

                is Routes.ModelManagement -> NavEntry(key) {
                    ModelManagementPage(
                        onNavigateBack = { popBack() }
                    )
                }
            }
        }
    )

}