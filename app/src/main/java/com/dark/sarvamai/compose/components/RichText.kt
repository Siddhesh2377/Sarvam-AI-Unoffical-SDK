package com.dark.sarvamai.compose.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun RichText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    textAlign: TextAlign? = TextAlign.Start,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    // Pre-compile regex patterns
    val headingRegex      = remember { Regex("^(#{1,6})\\s+(.*)$") }
    val numberedListRegex = remember { Regex("^(\\d+)\\.\\s+(.*)$") }
    val bulletRegex       = remember { Regex("^[-*+]\\s+(.*)$") }

    // Map heading levels to span styles
    val headingStyles =
        listOf(
            MaterialTheme.typography.headlineLarge.toSpanStyle(),
            MaterialTheme.typography.headlineMedium.toSpanStyle(),
            MaterialTheme.typography.headlineSmall.toSpanStyle(),
            MaterialTheme.typography.titleLarge.toSpanStyle(),
            MaterialTheme.typography.titleMedium.toSpanStyle(),
            MaterialTheme.typography.titleSmall.toSpanStyle()
        )


    // Preprocess input: inject newline before every numbered list marker
    val safeText = remember(text) {
        text.replace(Regex("(\\d+)\\.\\s"), "\n$1. ").trim()
    }

    // Build annotated string when safeText changes
    val annotatedText = remember(safeText) {
        buildAnnotatedString {
            safeText.lines().forEach { line ->
                when {
                    headingRegex.matches(line) -> {
                        val (hashes, content) = headingRegex.find(line)!!.destructured
                        val level = (hashes.length - 1).coerceIn(0, 5)
                        withStyle(headingStyles[level]) {
                            append(content)
                        }
                        append("\n\n")
                    }
                    numberedListRegex.matches(line) -> {
                        val (num, content) = numberedListRegex.find(line)!!.destructured
                        append("$num. ")
                        appendStyledSegment(content)
                        append("\n\n")
                    }
                    bulletRegex.matches(line) -> {
                        val content = bulletRegex.find(line)!!.groupValues[1]
                        append("• ")
                        appendStyledSegment(content)
                        append("\n\n")
                    }
                    else -> {
                        appendStyledSegment(line)
                    }
                }
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign
    )
}

// Extension for **bold** and *italic*
private fun AnnotatedString.Builder.appendStyledSegment(segment: String) {
    var i = 0
    while (i < segment.length) {
        when {
            segment.startsWith("**", i) -> {
                val end = segment.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(segment.substring(i + 2, end))
                    }
                    i = end + 2
                    continue
                }
            }
            segment.startsWith("*", i) -> {
                val end = segment.indexOf("*", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(segment.substring(i + 1, end))
                    }
                    i = end + 1
                    continue
                }
            }
        }
        append(segment[i])
        i++
    }
}
