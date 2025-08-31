package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.brucehsieh.logneko.data.modal.LineItem

@Composable
fun LineText(
    lineItem: LineItem,
    modifier: Modifier = Modifier,
    matchRanges: List<IntRange>,
    activeOccurrenceIndex: Int?
) {
    if (matchRanges.isNotEmpty()) {
        HighlightText(text = lineItem.text, modifier = modifier, intRanges = matchRanges, activeOccurrenceIndex)
    } else
        Text(text = lineItem.text, modifier = modifier)
}