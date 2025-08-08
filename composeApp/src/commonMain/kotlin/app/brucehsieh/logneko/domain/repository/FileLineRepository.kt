package app.brucehsieh.logneko.domain.repository

import androidx.paging.PagingData
import app.brucehsieh.logneko.data.modal.LineItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing file lines.
 */
interface FileLineRepository {

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
}