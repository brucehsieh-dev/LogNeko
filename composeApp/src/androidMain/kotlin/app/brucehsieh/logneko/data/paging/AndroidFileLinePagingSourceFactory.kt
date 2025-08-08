package app.brucehsieh.logneko.data.paging

import androidx.core.net.toUri
import androidx.paging.PagingSource
import app.brucehsieh.logneko.core.paging.FileLinePagingSourceFactory

class AndroidFileLinePagingSourceFactory(
    private val uriString: String
) : FileLinePagingSourceFactory {

    override fun createPagingSource(pageSize: Int): PagingSource<Int, String> {
        val uri = uriString.toUri()
        return ContentUriLinePagingSource(uri, pageSize)
    }
}