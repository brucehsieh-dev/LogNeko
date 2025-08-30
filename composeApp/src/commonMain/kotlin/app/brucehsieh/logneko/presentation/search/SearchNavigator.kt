package app.brucehsieh.logneko.presentation.search

import app.brucehsieh.logneko.presentation.modal.SearchHit

/**
 * Contract for search navigation in the UI.
 *
 * Keeps track of search hits and the current active hit, with helpers to update and navigate between them.
 *
 * @property searchHits The current list of flattened hits.
 * @property activeSearchHitIndex Index of the active hit, or -1 if none.
 */
interface SearchNavigator {
    val searchHits: List<SearchHit>
    val activeSearchHitIndex: Int

    /**
     * Rebuilds the list of [searchHits] from [matchesByLine].
     *
     * @param matchesByLine Map of line numbers to match ranges.
     * @param keepSelection If true, try to keep the current active hit.
     */
    suspend fun update(matchesByLine: Map<Int, List<IntRange>>, keepSelection: Boolean = true)

    /**
     * Move to the next hit, wrapping to the first if at the end.
     */
    fun next()

    /**
     * Move to the previous hit, wrapping to the last if at the start.
     */
    fun prev()

    /**
     * Focus the first hit on a given [lineNumber].
     *
     * @return true if a hit was found and selected, false otherwise.
     */
    fun focusLine(lineNumber: Int): Boolean
}