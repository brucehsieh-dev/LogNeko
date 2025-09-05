package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.paging.compose.LazyPagingItems
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.LineSource
import app.brucehsieh.logneko.presentation.modal.SearchHit

/**
 * Pane that displays log lines with line numbers and text content.
 *
 * Depending on [lineSource], it either:
 * - Shows a paged list ([PagingNumberTextLazyList]) when lines are streamed via Paging.
 * - Shows a fully-loaded list ([NonPagingNumberTextList]) when all lines are in memory (e.g. after filtering).
 *
 * Features:
 * - Highlights text ranges via [matchesByLine].
 * - Keeps track of active search hit and auto-scrolls to it in FULL_LIST mode.
 * - Wraps the entire list in [SelectionContainer] to allow text selection without per-item overhead.
 *
 * @param lineSource         Source type for displaying lines (paged vs full).
 * @param displayedLineItems Fully-loaded list of items (used in FULL_LIST mode).
 * @param pagingItems        Paged list of items (used in PAGING mode).
 * @param listState          Scroll state shared with vertical scrollbar.
 * @param matchesByLine      Map of line number → list of text match ranges.
 * @param searchHits         All search hits currently available.
 * @param activeSearchHitIndex Index of the active search hit, or -1 if none.
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
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        SelectionContainer {
            when (lineSource) {
                LineSource.PAGING -> PagingNumberTextLazyList(
                    lineItems = pagingItems,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
                    matchesByLine = matchesByLine,
                    fontSize = fontSize
                )

                LineSource.FULL_LIST -> {
                    val activeSearchHit = searchHits.getOrNull(activeSearchHitIndex)

                    // Build lookup: line number → index in filtered list.
                    val lineNumberToIndex by remember(displayedLineItems) {
                        derivedStateOf {
                            HashMap<Int, Int>(displayedLineItems.size).apply {
                                displayedLineItems.forEachIndexed { index, item -> put(item.number, index) }
                            }
                        }
                    }

                    // Auto-scroll to active search hit if present.
                    LaunchedEffect(activeSearchHit, displayedLineItems) {
                        val targetLineNumber = activeSearchHit?.lineNumber ?: return@LaunchedEffect
                        val targetIndex = lineNumberToIndex[targetLineNumber]
                        if (targetIndex != null) {
                            listState.scrollToItem(index = targetIndex, scrollOffset = 0)
                        }
                    }

                    NonPagingNumberTextList(
                        displayedLineItems = displayedLineItems,
                        listState = listState,
                        modifier = Modifier.fillMaxSize(),
                        matchesByLine = matchesByLine,
                        activeSearchHit = activeSearchHit,
                        fontSize = fontSize
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
