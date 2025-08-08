package app.brucehsieh.logneko.data.paging

import androidx.core.net.toUri
import androidx.paging.PagingSource
import app.brucehsieh.logneko.core.paging.FileLinePagingSourceFactory
import app.brucehsieh.logneko.data.modal.LineItem

class AndroidFileLinePagingSourceFactory(private val uriString: String) : FileLinePagingSourceFactory {

    override fun createPagingSource(pageSize: Int): PagingSource<Int, LineItem> {
        val uri = uriString.toUri()
        return ContentUriLinePagingSource(uri, pageSize)
    }
}