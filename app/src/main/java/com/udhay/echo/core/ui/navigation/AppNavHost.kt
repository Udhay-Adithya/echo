package com.udhay.echo.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.udhay.echo.feature.chat.presentation.screen.ChatPage
import com.udhay.echo.feature.models.presentation.screen.ModelManagementPage
import com.udhay.echo.feature.settings.presentation.screen.ConnectionSettingsPage
import com.udhay.echo.feature.settings.presentation.screen.ModelSettingsPage
import com.udhay.echo.feature.settings.presentation.screen.PersonalizationPage
import com.udhay.echo.feature.settings.presentation.screen.SettingsPage
import com.udhay.echo.feature.tools.presentation.screen.ToolsPage

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
                        onNavigateToModelManagement = { navigateTo(Routes.ModelManagement) },
                        onNavigateToTools = { navigateTo(Routes.Tools) }
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

                is Routes.Tools -> NavEntry(key) {
                    ToolsPage(
                        onNavigateBack = { popBack() }
                    )
                }
            }
        }
    )

}