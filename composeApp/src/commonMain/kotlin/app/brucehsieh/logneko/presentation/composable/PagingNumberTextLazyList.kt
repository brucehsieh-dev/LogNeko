package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.ItemType

/**
 * Lazy list showing a line-number gutter and line text.
 *
 * Key points:
 * - `peek()` in key/contentType: decide type without triggering loads.
 * - Separate placeholder vs loaded rows via `contentType` for better reuse.
 * - Cache highlight lookups with `derivedStateOf` to avoid recomputation.
 */
@Composable
fun PagingNumberTextLazyList(
    lineItems: LazyPagingItems<LineItem>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    matchesByLine: Map<Int, List<IntRange>> = emptyMap(),
    fontSize: TextUnit
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(
            count = lineItems.itemCount,
            key = { index -> lineItems.peek(index)?.number ?: "placeholder-$index" },
            contentType = { index ->
                if (lineItems.peek(index) == null)
                    ItemType.Placeholder
                else
                    ItemType.NumberTextLine
            }
        ) { index ->
            val lineItem = lineItems[index] ?: return@items

            val matchRanges by remember(lineItem.number, matchesByLine) {
                derivedStateOf {
                    matchesByLine[lineItem.number].orEmpty()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                LineNumber(
                    lineItem = lineItem,
                    modifier = Modifier.width(64.dp),
                    fontSize = fontSize,
                    lineHeight = fontSize * 1.2f
                )
                LineText(
                    lineItem = lineItem,
                    matchRanges = matchRanges,
                    activeOccurrenceIndex = null,
                    fontSize = fontSize,
                    lineHeight = fontSize * 1.2f
                )
            }
        }
    }
}