package app.brucehsieh.logneko.domain.manager

import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.modal.Match

interface TextSearchManager {
    suspend fun findOccurrences(query: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<Match>
    suspend fun filter(query: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<LineItem>
}