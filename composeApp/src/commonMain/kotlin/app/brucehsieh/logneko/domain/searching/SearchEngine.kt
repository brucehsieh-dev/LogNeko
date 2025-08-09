package app.brucehsieh.logneko.domain.searching

import app.brucehsieh.logneko.data.modal.LineItem
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
import java.io.File

/**
 * Search engine interface.
 */
interface SearchEngine {

    /**
     * The file system directory to index.
     */
    val fsDirectory: FSDirectory

    /**
     * Index a file.
     *
     * @param file The file to index.
     */
    suspend fun index(file: File)

    /**
     * Search for a query string.
     *
     * @param file The file to search in.
     * @param queryString The query string to search for.
     * @param clauseCount The maximum number of clauses to search for.
     */
    suspend fun search(
        file: File,
        queryString: String,
        clauseCount: Int = IndexSearcher.getMaxClauseCount()
    ): List<LineItem>
}