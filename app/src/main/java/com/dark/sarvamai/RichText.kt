package com.dark.sarvamai

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun RichText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontFamily: FontFamily = FontFamily.Serif,
    fontWeight: FontWeight = FontWeight.Light
) {

    var level by remember { mutableIntStateOf(0) }

    var list = listOf<SpanStyle>(
        MaterialTheme.typography.headlineLarge.toSpanStyle(),
        MaterialTheme.typography.headlineMedium.toSpanStyle(),
        MaterialTheme.typography.headlineSmall.toSpanStyle(),
        MaterialTheme.typography.titleLarge.toSpanStyle(),
        MaterialTheme.typography.titleMedium.toSpanStyle(),
        MaterialTheme.typography.titleSmall.toSpanStyle()
    )


    val annotatedText = remember(text) {
        buildAnnotatedString {
            val lines = text.lines()
            lines.forEach { line ->
                // Heading detection (#, ##, ###...)
                val headingMatch = Regex("^(#{1,6})\\s+(.*)").find(line)
                if (headingMatch != null) {
                    level = headingMatch.groupValues[1].length
                    val content = headingMatch.groupValues[2]
                    withStyle(list[level]) {
                        append(content)
                    }
                    append("\n")
                } else if (line.trimStart().startsWith("•")) {
                    // Bullet list
                    append("• ")
                    val content = line.removePrefix("•").trimStart()
                    appendStyledSegment(content)
                    append("\n")
                } else {
                    // Regular line
                    appendStyledSegment(line)
                    append("\n")
                }
            }
        }
    }

    Text(
        text = annotatedText,
        style = style,
        color = color,
        textAlign = TextAlign.Justify,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        modifier = modifier.padding(4.dp)
    )
}

private fun AnnotatedString.Builder.appendStyledSegment(line: String) {
    when {
        // Bold (**text**)
        line.contains("**") -> {
            val parts = line.split("**")
            parts.forEachIndexed { index, part ->
                if (index % 2 == 0) append(part)
                else withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append(part) }
            }
        }
        // Italic (*text*)
        line.contains("*") -> {
            val parts = line.split("*")
            parts.forEachIndexed { index, part ->
                if (index % 2 == 0) append(part)
                else withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(part) }
            }
        }
        // Plain text
        else -> append(line)
    }
}
