package app.brucehsieh.logneko.data.paging

import android.content.Context
import androidx.core.net.toUri
import app.brucehsieh.logneko.core.util.DefaultCoroutineDispatchers
import app.brucehsieh.logneko.data.modal.LineItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.getValue

/**
 * LineReader for Android content:// URIs (SAF).
 */
class ContentUriLineReader(
    private val ioDispatcher: CoroutineDispatcher = DefaultCoroutineDispatchers.io
) : LineReader, KoinComponent {

    private val context get() = get<Context>()
    private val uri by lazy { filePath.toUri() }

    override lateinit var filePath: String

    override suspend fun readLines(startLine: Int, count: Int): List<String> = withContext(ioDispatcher) {
        val out = ArrayList<String>(count.coerceAtMost(4096))

        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            var lineNumber = 0

            // Skip until startLine
            while (lineNumber < startLine) {
                ensureActive()
                if (reader.readLine() == null) return@use // EOF before start
                lineNumber++
            }

            // Read `count` lines
            var loaded = 0
            while (loaded < count) {
                ensureActive()
                val lineText = reader.readLine() ?: break
                out.add(lineText)
                loaded++
            }
        }

        out
    }

    override suspend fun readAll(progress: (Int) -> Unit): List<LineItem> = withContext(ioDispatcher) {
        context.contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
            val out = ArrayList<LineItem>(32_768)
            var lineNumber = 0
            while (true) {
                ensureActive()
                val lineText = reader?.readLine() ?: break
                lineNumber++
                out.add(LineItem(lineNumber, lineText))
                if (lineNumber % 1000 == 0) progress(lineNumber)
            }
            out
        }
    }
}