package com.udhay.echo.core.ui.navigation

sealed class Routes() {
    object Chat: Routes()
    object Settings: Routes()
    object Personalization: Routes()
    object ConnectionSettings: Routes()
    object ModelSettings: Routes()
    object ModelManagement: Routes()
    object Tools: Routes()
}