package app.brucehsieh.logneko.data.paging

import androidx.paging.PagingSource
import app.brucehsieh.logneko.core.paging.FileLinePagingSourceFactory
import app.brucehsieh.logneko.data.paging.FileLinePagingSource

class JvmFileLinePagingSourceFactory(
    private val filePath: String
) : FileLinePagingSourceFactory {

    // TODO: take platform file instead of a file path

    override fun createPagingSource(pageSize: Int): PagingSource<Int, String> {
        return FileLinePagingSource(filePath, pageSize)
    }
}