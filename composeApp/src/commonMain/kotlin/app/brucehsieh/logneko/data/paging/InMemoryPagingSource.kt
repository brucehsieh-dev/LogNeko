package app.brucehsieh.logneko.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.brucehsieh.logneko.data.modal.LineItem

class InMemoryPagingSource(
    private val lines: List<String>,
    private val pageSize: Int
) : PagingSource<Int, LineItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LineItem> {
        val startLine = params.key ?: 0
        if (startLine >= lines.size) return LoadResult.Page(emptyList(), null, null)

        // Calculate the end line
        val endExclusive = (startLine + pageSize).coerceAtMost(lines.size)

        return LoadResult.Page(
            // Get the slice of lines from the list, and map them to LineItem objects
            data = lines.subList(startLine, endExclusive).mapIndexed { idx, text ->
                LineItem(number = startLine + idx + 1, text = text)
            },
            prevKey = if (startLine == 0) null else (startLine - pageSize).coerceAtLeast(0),
            nextKey = if (endExclusive >= lines.size) null else endExclusive
        )
    }

    override fun getRefreshKey(state: PagingState<Int, LineItem>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }
}