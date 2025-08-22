package app.brucehsieh.logneko.data.paging

import androidx.paging.PagingSource
import app.brucehsieh.logneko.data.paging.FileLinePagingSourceFactory
import app.brucehsieh.logneko.data.modal.LineItem

class JvmFileLinePagingSourceFactory(private val filePath: String) : FileLinePagingSourceFactory {

    override fun createPagingSource(pageSize: Int): PagingSource<Int, LineItem> {
        return FileLinePagingSource(filePath, pageSize)
    }
}