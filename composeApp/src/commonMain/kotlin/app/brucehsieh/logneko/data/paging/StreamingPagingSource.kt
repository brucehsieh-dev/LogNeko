package app.brucehsieh.logneko.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.brucehsieh.logneko.data.modal.LineItem

class StreamingPagingSource(
    private val lineReader: LineReader,
    private val pageSize: Int
) : PagingSource<Int, LineItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LineItem> {
        val startLine = params.key ?: 0
        return try {
            val lines = lineReader.readLines(startLine, pageSize)
            val items = lines.mapIndexed { idx, text ->
                LineItem(number = startLine + idx + 1, text = text)
            }
            LoadResult.Page(
                data = items,
                prevKey = if (startLine == 0) null else (startLine - pageSize).coerceAtLeast(0),
                nextKey = if (lines.size < pageSize) null else startLine + lines.size
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LineItem>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }
}