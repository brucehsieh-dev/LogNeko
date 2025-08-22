package app.brucehsieh.logneko.domain.manager

import app.brucehsieh.logneko.core.util.DefaultCoroutineDispatchers
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.modal.Match
import app.brucehsieh.logneko.domain.searching.InMemorySearcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultTextSearchManager(
    private val inMemorySearcher: InMemorySearcher,
    private val ioDispatcher: CoroutineDispatcher = DefaultCoroutineDispatchers.io
) : TextSearchManager {

    override suspend fun findOccurrences(textQuery: String, ignoreCase: Boolean, limit: Int): List<Match> =
        withContext(ioDispatcher) {
            inMemorySearcher.search(textQuery, ignoreCase, limit)
        }

    override suspend fun filter(filterQuery: String, ignoreCase: Boolean, limit: Int): List<LineItem> =
        withContext(ioDispatcher) {
            inMemorySearcher.filter(filterQuery, ignoreCase, limit)
        }
}