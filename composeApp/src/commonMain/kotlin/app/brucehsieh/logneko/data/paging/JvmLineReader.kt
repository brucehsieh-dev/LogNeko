package app.brucehsieh.logneko.data.paging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Paths

class JvmLineReader(private val filePath: String) : LineReader {

    override suspend fun readLines(startLine: Int, count: Int): List<String> = withContext(Dispatchers.IO) {
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

    override suspend fun readAll(progress: (Int) -> Unit): List<String> = withContext(Dispatchers.IO) {
        Files.newBufferedReader(Paths.get(filePath)).useLines { sequence ->
            // Convert the sequence into an array
            val out = ArrayList<String>(32_768)
            var c = 0
            sequence.forEach {
                out.add(it); c++
                if (c % 1000 == 0) progress(c)
            }
            out
        }
    }
}