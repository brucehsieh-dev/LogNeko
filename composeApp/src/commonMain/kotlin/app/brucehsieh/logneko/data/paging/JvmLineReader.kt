package app.brucehsieh.logneko.data.paging

import app.brucehsieh.logneko.core.util.DefaultCoroutineDispatchers
import app.brucehsieh.logneko.data.modal.LineItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Paths

class JvmLineReader(
    private val filePath: String,
    private val ioDispatcher: CoroutineDispatcher = DefaultCoroutineDispatchers.io
) : LineReader {

    override suspend fun readLines(startLine: Int, count: Int): List<String> = withContext(ioDispatcher) {
        RandomAccessFile(filePath, "r").use { raf ->

            // Skip lines before startLine
            var currentLine = 0
            while (currentLine < startLine && raf.readLine() != null) {
                currentLine++
            }

            // Read the next 'count' lines
            val out = ArrayList<String>(count)
            var loaded = 0
            while (loaded < count && raf.readLine()?.also { out.add(it) } != null) {
                loaded++
            }
            out
        }
    }

    override suspend fun readAll(progress: (Int) -> Unit): List<LineItem> = withContext(ioDispatcher) {
        Files.newBufferedReader(Paths.get(filePath)).useLines { sequence ->
            val out = ArrayList<LineItem>(32_768)
            var lineNumber = 0
            for (text in sequence) {
                lineNumber += 1
                out.add(LineItem(lineNumber, text))
                if (lineNumber % 1000 == 0) progress(lineNumber)
            }
            out
        }
    }
}