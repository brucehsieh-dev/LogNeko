package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.ItemType
import app.brucehsieh.logneko.presentation.modal.SearchHit

/**
 * Displays a fully realized list of numbered text lines without paging.
 *
 * Used when lines are already loaded in memory (either all lines or a filtered subset).
 *
 * @param displayedLineItems The in-memory list of [LineItem]s to render.
 * @param listState The [LazyListState] controlling and observing scroll position.
 * @param matchesByLine Map of line numbers to match ranges for highlighting search results.
 * @param activeSearchHit The currently focused search hit (line number + occurrence index), or null if none.
 * @param modifier [Modifier] for styling and layout of the surrounding [LazyColumn].
 */
@Composable
fun NonPagingNumberTextList(
    displayedLineItems: List<LineItem>,
    listState: LazyListState,
    matchesByLine: Map<Int, List<IntRange>>,
    activeSearchHit: SearchHit?,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    val activeSearchHitLineNumber = activeSearchHit?.lineNumber
    val activeSearchHitOccurrenceIndex = activeSearchHit?.occurrenceIndex

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = displayedLineItems,
            key = { it.number },                        // stable identity
            contentType = { ItemType.NumberTextLine }   // constant type; no placeholders
        ) { lineItem ->
            val matchRanges by remember(lineItem.number, matchesByLine) {
                derivedStateOf { matchesByLine[lineItem.number].orEmpty() }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                LineNumber(lineItem = lineItem, modifier = Modifier.width(64.dp), fontSize = fontSize)
                LineText(
                    lineItem = lineItem,
                    matchRanges = matchRanges,
                    // Only non-null for the active hit on this line
                    activeOccurrenceIndex =
                        if (activeSearchHitLineNumber != lineItem.number) null
                        else activeSearchHitOccurrenceIndex,
                    fontSize = fontSize
                )
            }
        }
    }
}