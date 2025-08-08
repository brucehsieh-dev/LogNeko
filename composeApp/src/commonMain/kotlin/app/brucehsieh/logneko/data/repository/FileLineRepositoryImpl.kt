package app.brucehsieh.logneko.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.brucehsieh.logneko.core.paging.FileLinePagingSourceFactory
import app.brucehsieh.logneko.data.CONTENT_URL
import app.brucehsieh.logneko.data.JVM_FILE
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class FileLineRepositoryImpl : FileLineRepository, KoinComponent {

    override fun getFileLinesPagedByPath(filePath: String, pageSize: Int): Flow<PagingData<LineItem>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                get<FileLinePagingSourceFactory>(named(JVM_FILE)) { parametersOf(filePath) }
                    .createPagingSource(pageSize)
            }
        ).flow

    override fun getFileLinesPagedByContentUri(contentUriString: String, pageSize: Int): Flow<PagingData<LineItem>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                get<FileLinePagingSourceFactory>(named(CONTENT_URL)) { parametersOf(contentUriString) }
                    .createPagingSource(pageSize)
            }
        ).flow
}
