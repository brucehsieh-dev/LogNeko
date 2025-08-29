package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult

/**
 * Displays [text] with highlighted segments.
 *
 * @param text Full line text to render.
 * @param intRanges Character ranges to highlight in [text].
 *   - 0-based indices, [start..endExclusive)
 *   - Pass an empty list for no highlights.
 * @param modifier Optional layout modifier.
 */
@Composable
fun HighlightText(
    text: String,
    modifier: Modifier = Modifier,
    intRanges: List<IntRange> = emptyList(),
    activeOccurrenceIndex: Int?,
    maxHighlightsPerLine: Int = Int.MAX_VALUE
) {
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }

    val drawModifier = modifier.drawBehind {
        val l = layout ?: return@drawBehind
        val budgeted = if (intRanges.size > maxHighlightsPerLine)
            intRanges.subList(0, maxHighlightsPerLine) else intRanges

        budgeted.forEachIndexed { i, intRange ->
            val start = intRange.first.coerceAtLeast(0)
            val end = (intRange.last + 1).coerceAtMost(text.length) // endExclusive
            val path = l.getPathForRange(start, end)
            drawPath(path, if (i == activeOccurrenceIndex) Color.Green else Color.Gray)
        }
    }

    Text(
        text = text,
        onTextLayout = { layout = it },
        modifier = drawModifier
    )
}