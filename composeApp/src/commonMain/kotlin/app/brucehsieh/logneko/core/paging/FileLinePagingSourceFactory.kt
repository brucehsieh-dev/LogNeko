package app.brucehsieh.logneko.core.paging

import androidx.paging.PagingSource

fun interface FileLinePagingSourceFactory {
    fun createPagingSource(pageSize: Int): PagingSource<Int, String>
}