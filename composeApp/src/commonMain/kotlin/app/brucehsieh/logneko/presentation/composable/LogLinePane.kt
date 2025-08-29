package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.LineSource
import app.brucehsieh.logneko.presentation.modal.SearchHit

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
    lineSource: LineSource,
    displayedLineItems: List<LineItem>,
    pagingItems: LazyPagingItems<LineItem>,
    listState: LazyListState,
    matchesByLine: Map<Int, List<IntRange>>,
    searchHits: List<SearchHit>,
    activeSearchHitIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        SelectionContainer {
            when (lineSource) {
                LineSource.PAGING -> PagingNumberTextLazyList(
                    lineItems = pagingItems,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
                    matchesByLine = matchesByLine
                )

                LineSource.FULL_LIST -> {
                    val activeSearchHit = searchHits.getOrNull(activeSearchHitIndex)
                    LaunchedEffect(activeSearchHit) {
                        val target = activeSearchHit?.lineNumber?.minus(1)
                        if (target != null) {
                            listState.scrollToItem(target)
                        }
                    }

                    NonPagingNumberTextList(
                        displayedLineItems = displayedLineItems,
                        listState = listState,
                        modifier = Modifier.fillMaxSize(),
                        matchesByLine = matchesByLine,
                        activeSearchHit = activeSearchHit
                    )
                }
            }
        }

        DesktopVerticalScroll(
            lazyListState = listState,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}
