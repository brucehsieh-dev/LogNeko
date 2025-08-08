package app.brucehsieh.logneko.core.paging

import androidx.paging.PagingSource
import app.brucehsieh.logneko.data.modal.LineItem

fun interface FileLinePagingSourceFactory {
    fun createPagingSource(pageSize: Int): PagingSource<Int, LineItem>
}