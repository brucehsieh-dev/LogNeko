package app.brucehsieh.logneko.presentation.search

import app.brucehsieh.logneko.core.util.DefaultCoroutineDispatchers
import app.brucehsieh.logneko.presentation.modal.SearchHit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultSearchNavigator(
    private val defaultDispatcher: CoroutineDispatcher = DefaultCoroutineDispatchers.default
) : SearchNavigator {

    override var searchHits: List<SearchHit> = emptyList()
        private set
    override var activeSearchHitIndex: Int = -1
        private set

    override suspend fun update(matchesByLine: Map<Int, List<IntRange>>, keepSelection: Boolean) =
        withContext(defaultDispatcher) {
            val newSearchHits = ArrayList<SearchHit>()
            matchesByLine.iterator().forEach { (lineNumber, ranges) ->
                ranges.forEachIndexed { idx, _ ->
                    newSearchHits += SearchHit(lineNumber = lineNumber, occurrenceIndex = idx)
                }
            }

            val oldActiveSearchHitIndex = searchHits.getOrNull(activeSearchHitIndex)
            searchHits = newSearchHits
            activeSearchHitIndex = when {
                searchHits.isEmpty() -> -1
                !keepSelection -> 0
                else -> searchHits
                    .indexOf(oldActiveSearchHitIndex)
                    .takeIf { it >= 0 } ?: activeSearchHitIndex.coerceIn(0, searchHits.lastIndex)
            }
        }


    override fun next() {
        if (searchHits.isNotEmpty()) activeSearchHitIndex = (activeSearchHitIndex + 1).mod(searchHits.size)
    }

    override fun prev() {
        if (searchHits.isNotEmpty()) activeSearchHitIndex = (activeSearchHitIndex - 1).mod(searchHits.size)
    }

    override fun focusLine(lineNumber: Int): Boolean {
        val idx = searchHits.indexOfFirst { it.lineNumber == lineNumber }
        return if (idx >= 0) {
            activeSearchHitIndex = idx; true
        } else false
    }
}