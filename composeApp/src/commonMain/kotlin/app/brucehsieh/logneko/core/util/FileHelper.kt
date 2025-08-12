package app.brucehsieh.logneko.core.util

import org.apache.lucene.store.FSDirectory
import java.nio.file.Paths

object FileHelper {
    private val indexPath by lazy { Paths.get("path/to/my-index") }
    val fsDirectory: FSDirectory? by lazy { FSDirectory.open(indexPath) }
}