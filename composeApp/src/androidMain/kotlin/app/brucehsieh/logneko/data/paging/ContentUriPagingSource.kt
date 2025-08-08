package app.brucehsieh.logneko.data.paging

import android.content.Context
import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.brucehsieh.logneko.data.modal.LineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ContentUriLinePagingSource(
    private val contentUri: Uri,
    private val pageSize: Int
) : PagingSource<Int, LineItem>(), KoinComponent {

    private val context get() = get<Context>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LineItem> {
        val startLine = params.key ?: 0

        return try {
            val lines = readLinesFromUri(context, contentUri, startLine, pageSize)
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

    private suspend fun readLinesFromUri(
        context: Context,
        uri: Uri,
        startLine: Int,
        count: Int
    ): List<String> = withContext(Dispatchers.IO) {
        val result = mutableListOf<String>()

        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            var currentLine = 0
            var line: String? = null

            // Skip lines before startLine
            while (currentLine < startLine && reader.readLine().also { line = it } != null) {
                currentLine++
            }

            // Read the next 'count' lines
            var loaded = 0
            while (loaded < count && reader.readLine().also { line = it } != null) {
                result.add(line ?: "")
                loaded++
            }
        }

        result
    }
}
