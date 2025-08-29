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
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.ItemType

/**
 * Filtered, fully-realized list of numbered text lines.
 *
 * Use this when all lines are already in memory (e.g., after applying filters). Unlike [PagingNumberTextLazyList], this
 * composable has no placeholders; every row uses a constant content type and a stable key.
 *
 * @param items         Lines to display. Each item must have a stable, unique [LineItem.number].
 * @param listState     Scroll state shared with external scrollbars, if any.
 * @param matchesByLine Map from line number to highlight ranges within that line.
 */
@Composable
fun FullNumberTextList(
    displayedLineItems: List<LineItem>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    matchesByLine: Map<Int, List<IntRange>> = emptyMap()
) {
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
                LineNumber(lineItem = lineItem, modifier = Modifier.width(64.dp))
                LineText(lineItem = lineItem, matchRanges = matchRanges)
            }
        }
    }
}