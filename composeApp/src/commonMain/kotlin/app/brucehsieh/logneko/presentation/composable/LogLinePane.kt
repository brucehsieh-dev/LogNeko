package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import app.brucehsieh.logneko.data.modal.LineItem

/**
 * Displays either the filtered fixed list or the paged list with numbers & highlights.
 *
 * Perf notes:
 * - `matchesByLine` is already memoized by the caller to avoid repeated map object identity changes.
 * - Filtered list mirrors the same `derivedStateOf` approach used by NumberTextLazyList to avoid
 *   recomputing highlights per item across recompositions.
 * - SelectionContainer wraps the entire list once to avoid per-item overhead.
 */
@Composable
fun LogLinePane(
    filteredLineItems: List<LineItem>,
    pagingItems: LazyPagingItems<LineItem>,
    listState: LazyListState,
    matchesByLine: Map<Int, List<IntRange>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        SelectionContainer {
            if (filteredLineItems.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = filteredLineItems.size,
                        key = { filteredLineItems[it].number }
                    ) { index ->
                        val lineItem = filteredLineItems[index]

                        val matchRanges by remember(lineItem.number, matchesByLine) {
                            derivedStateOf { matchesByLine[lineItem.number].orEmpty() }
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            LineNumber(lineItem = lineItem, modifier = Modifier.width(64.dp))
                            LineText(lineItem = lineItem, matchRanges = matchRanges)
                        }
                    }
                }
            } else {
                NumberTextLazyList(
                    lineItems = pagingItems,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
                    matchesByLine = matchesByLine
                )
            }
        }

        DesktopVerticalScroll(
            lazyListState = listState,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}
