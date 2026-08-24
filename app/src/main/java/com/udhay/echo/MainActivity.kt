package com.udhay.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.core.ui.navigation.AppNavHost
import com.udhay.echo.core.ui.theme.EchoTheme
import com.udhay.echo.feature.settings.presentation.viewmodel.UserSettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: UserSettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val isDark = settings.darkModeEnabled

            EchoTheme(
                darkTheme = isDark,
                isAmoled = settings.amoledPaletteEnabled,
                fontScale = settings.fontScale
            ) {
                val view = this@MainActivity.window.decorView

                SideEffect {
                    val window = this@MainActivity.window
                    val controller = WindowCompat.getInsetsController(window, view)
                    controller.isAppearanceLightStatusBars = !isDark
                }

                AppNavHost()
            }
        }
    }
}