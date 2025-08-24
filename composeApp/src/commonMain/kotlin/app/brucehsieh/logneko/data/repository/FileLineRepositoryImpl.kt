package app.brucehsieh.logneko.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.data.modal.PagingDataMode
import app.brucehsieh.logneko.data.paging.InMemoryPagingSource
import app.brucehsieh.logneko.data.paging.LineReader
import app.brucehsieh.logneko.data.paging.StreamingPagingSource
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import app.brucehsieh.logneko.domain.searching.InMemorySearcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class FileLineRepositoryImpl : FileLineRepository, KoinComponent {

    private val _pagingDataMode = MutableStateFlow(PagingDataMode.STREAMING)
    private val _allLines = MutableStateFlow(listOf<LineItem>())

    override val pagingDataMode: StateFlow<PagingDataMode> = _pagingDataMode.asStateFlow()
    override val allLines: StateFlow<List<LineItem>> = _allLines.asStateFlow()

    override fun streamPager(lineReader: LineReader, pageSize: Int): Flow<PagingData<LineItem>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StreamingPagingSource(lineReader, pageSize)
            }
        ).flow

    override fun memoryPager(lineReader: LineReader, pageSize: Int): Flow<PagingData<LineItem>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                InMemoryPagingSource(allLines.value, pageSize)
            }
        ).flow

    override fun fullLoaded(lineItems: List<LineItem>) {
        _allLines.value = lineItems
        _pagingDataMode.value = PagingDataMode.IN_MEMORY
        get<InMemorySearcher>().load(lineItems)
    }
}
