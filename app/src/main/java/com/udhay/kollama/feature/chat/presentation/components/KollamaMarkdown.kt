package com.udhay.kollama.feature.chat.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography

@Composable
fun KollamaMarkdown(
    content: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val typography = MaterialTheme.typography

    Markdown(
        content = content,
        modifier = modifier,
        retainState = true,
        colors = DefaultMarkdownColors(
            text = contentColor,
            codeBackground = contentColor.copy(alpha = 0.1f),
            inlineCodeBackground = contentColor.copy(alpha = 0.1f),
            dividerColor = contentColor.copy(alpha = 0.2f),
            tableBackground = contentColor.copy(alpha = 0.05f)
        ),
        typography = DefaultMarkdownTypography(
            h1 = typography.headlineLarge,
            h2 = typography.headlineMedium,
            h3 = typography.headlineSmall,
            h4 = typography.titleLarge,
            h5 = typography.titleMedium,
            h6 = typography.titleSmall,
            text = typography.bodyLarge,
            code = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            inlineCode = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            quote = typography.bodyLarge,
            paragraph = typography.bodyLarge,
            ordered = typography.bodyLarge,
            bullet = typography.bodyLarge,
            list = typography.bodyLarge,
            textLink = TextLinkStyles(),
            table = typography.bodyMedium
        )
    )
}
