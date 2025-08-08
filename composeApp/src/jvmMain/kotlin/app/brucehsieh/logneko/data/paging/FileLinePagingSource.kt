package app.brucehsieh.logneko.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.brucehsieh.logneko.data.modal.LineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile

/**
 * One page represents one line of text.
 */
class FileLinePagingSource(
    private val filePath: String,
    private val pageSize: Int
) : PagingSource<Int, LineItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LineItem> {
        val startLine = params.key ?: 0

        return try {
            val lines = readLinesFromFile(filePath, startLine, pageSize)
            val lineItems = lines.mapIndexed { idx, text ->
                LineItem(number = startLine + idx + 1, text = text)
            }
            LoadResult.Page(
                data = lineItems,
                prevKey = if (startLine == 0) null else (startLine - pageSize).coerceAtLeast(0),
                nextKey = if (lines.size < pageSize) null else startLine + lines.size
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LineItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
    }

    private suspend fun readLinesFromFile(path: String, startLine: Int, count: Int): List<String> =
        withContext(Dispatchers.IO) {
            val result = mutableListOf<String>()
            RandomAccessFile(path, "r").use { raf ->
                var currentLine = 0
                var line: String? = null

                // Skip lines before startLine
                while (currentLine < startLine && raf.readLine().also { line = it } != null) {
                    currentLine++
                }

                // Read only the requested number of lines
                var loaded = 0
                while (loaded < count && raf.readLine().also { line = it } != null) {
                    result.add(line ?: "")
                    loaded++
                }
            }
            result
        }
}