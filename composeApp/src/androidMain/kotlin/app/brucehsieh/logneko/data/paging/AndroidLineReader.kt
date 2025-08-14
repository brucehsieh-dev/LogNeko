package app.brucehsieh.logneko.data.paging

import android.content.Context
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.FileInputStream

class AndroidLineReader(private val uriString: String) : LineReader, KoinComponent {

    private val inputStream
        get() = if (uriString.startsWith("content://")) {
            get<Context>().contentResolver.openInputStream(uriString.toUri())
        } else {
            FileInputStream(uriString)
        }

    override suspend fun readLines(startLine: Int, count: Int): List<String> = withContext(Dispatchers.IO) {
        inputStream?.let { inputStream ->
            inputStream.bufferedReader().use { br ->

                // Skip lines before startLine
                var currentLine = 0
                while (currentLine < startLine && br.readLine() != null) {
                    currentLine++
                }

                // Read the next 'count' lines
                val out = ArrayList<String>(count)
                var loaded = 0
                while (loaded < count) {
                    val line = br.readLine() ?: break
                    out.add(line)
                    loaded++
                }
                out
            }
        } ?: throw IllegalStateException("Input stream is null")
    }

    override suspend fun readAll(progress: (Int) -> Unit): List<String> = withContext(Dispatchers.IO) {
        inputStream?.let { inputStream ->
            inputStream.bufferedReader().useLines { sequence ->
                // Convert the sequence into an array
                val out = ArrayList<String>(32_768)
                var count = 0
                sequence.forEach {
                    out.add(it)
                    count++
                    if (count % 1000 == 0) progress(count)
                }
                out
            }
        } ?: throw IllegalStateException("Input stream is null")
    }
}