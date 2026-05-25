package com.udhay.kollama.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun KollamaMarkdown(
    content: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val flavour = remember { GFMFlavourDescriptor() }
    val parsedTree = remember(content) {
        MarkdownParser(flavour).buildMarkdownTreeFromString(content)
    }

    Column(modifier = modifier) {
        parsedTree.children.forEach { node ->
            MarkdownNode(node, content, contentColor)
        }
    }
}

@Composable
private fun MarkdownNode(node: ASTNode, source: String, contentColor: Color) {
    when (node.type) {
        MarkdownElementTypes.ATX_1 -> Header(node, source, contentColor, MaterialTheme.typography.headlineLarge)
        MarkdownElementTypes.ATX_2 -> Header(node, source, contentColor, MaterialTheme.typography.headlineMedium)
        MarkdownElementTypes.ATX_3 -> Header(node, source, contentColor, MaterialTheme.typography.headlineSmall)
        MarkdownElementTypes.PARAGRAPH -> Paragraph(node, source, contentColor)
        MarkdownElementTypes.CODE_BLOCK, MarkdownElementTypes.CODE_FENCE -> CodeBlock(node, source, contentColor)
        MarkdownElementTypes.LIST_ITEM -> ListItem(node, source, contentColor)
        MarkdownElementTypes.ORDERED_LIST, MarkdownElementTypes.UNORDERED_LIST -> {
            Column {
                node.children.forEach { child ->
                    MarkdownNode(child, source, contentColor)
                }
            }
        }
        GFMElementTypes.STRIKETHROUGH -> Strikethrough(node, source, contentColor)
        else -> {
            // If it's not a block we handle, try to render children
            if (node.children.isNotEmpty()) {
                node.children.forEach { child ->
                    MarkdownNode(child, source, contentColor)
                }
            }
        }
    }
}

@Composable
private fun Header(node: ASTNode, source: String, contentColor: Color, style: androidx.compose.ui.text.TextStyle) {
    val text = node.getTextInNode(source).toString().trimStart('#', ' ').trim()
    Text(
        text = text,
        style = style,
        color = contentColor,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun Paragraph(node: ASTNode, source: String, contentColor: Color) {
    Text(
        text = buildMarkdownAnnotatedString(node, source, contentColor),
        style = MaterialTheme.typography.bodyLarge,
        color = contentColor,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun Strikethrough(node: ASTNode, source: String, contentColor: Color) {
    Text(
        text = buildMarkdownAnnotatedString(node, source, contentColor),
        style = MaterialTheme.typography.bodyLarge.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
        color = contentColor,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun CodeBlock(node: ASTNode, source: String, contentColor: Color) {
    // Extract content, skipping the first line if it's the language identifier
    val fullText = node.getTextInNode(source).toString().trim()
    val lines = fullText.split("\n")
    val codeLines = if (lines.size > 1 && lines[0].startsWith("```")) {
        lines.subList(1, lines.size - 1)
    } else {
        lines.map { it.removeSurrounding("```") }
    }
    val text = codeLines.joinToString("\n").trim()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = contentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = contentColor
        )
    }
}

@Composable
private fun ListItem(node: ASTNode, source: String, contentColor: Color) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = "• ", color = contentColor)
        Column {
            node.children.forEach { child ->
                if (child.type != MarkdownTokenTypes.LIST_BULLET) {
                    MarkdownNode(child, source, contentColor)
                }
            }
        }
    }
}

private fun buildMarkdownAnnotatedString(node: ASTNode, source: String, contentColor: Color): AnnotatedString {
    return buildAnnotatedString {
        appendNode(node, source, contentColor)
    }
}

private fun AnnotatedString.Builder.appendNode(node: ASTNode, source: String, contentColor: Color) {
    node.children.forEach { child ->
        val typeStr = child.type.toString()
        when (child.type) {
            MarkdownElementTypes.EMPH -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    child.children.forEach { grandChild ->
                        val gTypeStr = grandChild.type.toString()
                        if (gTypeStr == "TEXT" || grandChild.children.isNotEmpty()) {
                            appendNode(grandChild, source, contentColor)
                        } else if (grandChild.children.isEmpty() && gTypeStr != "EMPH" && !gTypeStr.contains("ASTERISK") && !gTypeStr.contains("UNDERSCORE")) {
                            append(grandChild.getTextInNode(source).toString())
                        }
                    }
                }
            }
            MarkdownElementTypes.STRONG -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    child.children.forEach { grandChild ->
                        val gTypeStr = grandChild.type.toString()
                        if (gTypeStr == "TEXT" || grandChild.children.isNotEmpty()) {
                            appendNode(grandChild, source, contentColor)
                        } else if (grandChild.children.isEmpty() && gTypeStr != "STRONG" && !gTypeStr.contains("ASTERISK") && !gTypeStr.contains("UNDERSCORE")) {
                            append(grandChild.getTextInNode(source).toString())
                        }
                    }
                }
            }
            MarkdownElementTypes.CODE_SPAN -> {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = contentColor.copy(alpha = 0.1f)
                    )
                ) {
                    child.children.forEach { grandChild ->
                        val gTypeStr = grandChild.type.toString()
                        if (gTypeStr == "TEXT") {
                            append(grandChild.getTextInNode(source).toString())
                        } else if (grandChild.children.isEmpty() && !gTypeStr.contains("BACKTICK")) {
                            append(grandChild.getTextInNode(source).toString())
                        }
                    }
                }
            }
            GFMElementTypes.STRIKETHROUGH -> {
                withStyle(SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)) {
                    child.children.forEach { grandChild ->
                        val gTypeStr = grandChild.type.toString()
                        if (gTypeStr == "TEXT" || grandChild.children.isNotEmpty()) {
                            appendNode(grandChild, source, contentColor)
                        } else if (grandChild.children.isEmpty() && gTypeStr != "STRIKETHROUGH" && !gTypeStr.contains("TILDE")) {
                            append(grandChild.getTextInNode(source).toString())
                        }
                    }
                }
            }
            MarkdownTokenTypes.TEXT -> append(child.getTextInNode(source).toString())
            MarkdownTokenTypes.WHITE_SPACE -> append(" ")
            MarkdownTokenTypes.EOL -> append("\n")
            else -> {
                if (child.children.isEmpty()) {
                    val text = child.getTextInNode(source).toString()
                    if (typeStr == "TEXT") {
                        append(text)
                    } else if (typeStr != "EMPH" && typeStr != "STRONG" && typeStr != "CODE_SPAN" && typeStr != "STRIKETHROUGH" &&
                        !typeStr.contains("ASTERISK") && !typeStr.contains("UNDERSCORE") && !typeStr.contains("BACKTICK") && !typeStr.contains("TILDE")
                    ) {
                        append(text)
                    }
                } else {
                    appendNode(child, source, contentColor)
                }
            }
        }
    }
}
