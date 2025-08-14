package app.brucehsieh.logneko.domain.repository

import androidx.paging.PagingData
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.data.modal.PagingDataMode
import app.brucehsieh.logneko.data.paging.LineReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing file lines.
 */
interface FileLineRepository {

    val pagingDataMode: StateFlow<PagingDataMode>
    val allLines: StateFlow<List<String>>

    fun streamPager(lineReader: LineReader, pageSize: Int): Flow<PagingData<LineItem>>
    fun memoryPager(lineReader: LineReader, pageSize: Int): Flow<PagingData<LineItem>>

    /**
     * Retrieves a Flow of PagingData containing file lines.
     *
     * @param filePath The path to the file.
     * @param pageSize The number of lines per page. One page corresponds to one line of text.
     */
    fun getFileLinesPagedByPath(filePath: String, pageSize: Int): Flow<PagingData<LineItem>>

    /**
     * Retrieves a Flow of PagingData containing file lines.
     *
     * @param contentUriString The content URI of the file, Android specific.
     * @param pageSize The number of lines per page. One page corresponds to one line of text.
     */
    fun getFileLinesPagedByContentUri(contentUriString: String, pageSize: Int): Flow<PagingData<LineItem>>

    fun fullLoaded(lines: List<String>)
}