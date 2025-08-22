package app.brucehsieh.logneko.domain.manager

import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.modal.Match

/**
 * Manager that handles text searching and filtering.
 */
interface TextSearchManager {
    suspend fun findOccurrences(textQuery: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<Match>
    suspend fun filter(filterQuery: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<LineItem>
}