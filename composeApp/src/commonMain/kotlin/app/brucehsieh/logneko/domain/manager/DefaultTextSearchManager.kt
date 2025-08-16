package app.brucehsieh.logneko.domain.manager

import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.modal.Match
import app.brucehsieh.logneko.domain.searching.InMemorySearcher
import app.brucehsieh.logneko.domain.manager.TextSearchManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultTextSearchManager(
    private val inMemorySearcher: InMemorySearcher,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TextSearchManager {

    override suspend fun findOccurrences(query: String, ignoreCase: Boolean, limit: Int): List<Match> =
        withContext(dispatcher) {
            inMemorySearcher.search(query, ignoreCase, limit)
        }

    override suspend fun filter(query: String, ignoreCase: Boolean, limit: Int): List<LineItem> =
        withContext(dispatcher) {
            inMemorySearcher.filterLineItems(query, ignoreCase, limit)
        }
}