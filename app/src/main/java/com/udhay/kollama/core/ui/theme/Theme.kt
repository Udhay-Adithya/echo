package com.udhay.kollama.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

@Composable
fun KollamaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    isAmoled: Boolean = false,
    fontScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val generatedColorScheme =
        rememberDynamicColorScheme(
            seedColor = seedColor,
            isDark = darkTheme,
            isAmoled = isAmoled,
            style = PaletteStyle.Fidelity,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
        )

    val baseDensity = LocalDensity.current
    val scaledDensity = Density(
        density = baseDensity.density,
        fontScale = baseDensity.fontScale * fontScale
    )

    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        MaterialTheme(
            colorScheme = generatedColorScheme,
            typography = Typography,
            content = content
        )
    }
}