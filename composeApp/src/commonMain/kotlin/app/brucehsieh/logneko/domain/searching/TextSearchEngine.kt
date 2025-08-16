package app.brucehsieh.logneko.domain.searching

import app.brucehsieh.logneko.data.modal.LineItem
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntPoint
import org.apache.lucene.document.LongPoint
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.queryparser.classic.QueryParserBase
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.FSDirectory
import java.io.File

class TextSearchEngine(override val fsDirectory: FSDirectory) : SearchEngine {
    private val analyzer = StandardAnalyzer()
    private val indexWriterConfig
        get() = IndexWriterConfig(analyzer).apply {
            openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND
        }

    override suspend fun index(file: File) {
        IndexWriter(fsDirectory, indexWriterConfig).use { indexWriter ->

            indexWriter.deleteDocuments(Term(ABSOLUTE_PATH, file.absolutePath))

            file.useLines { lines ->
                lines.forEachIndexed { index, line ->
                    val lineNumber = index + 1
                    val document = Document().apply {
                        add(StringField(FILENAME, file.name, Field.Store.YES)) // for
                        add(StringField(ABSOLUTE_PATH, file.absolutePath, Field.Store.YES))
                        add(TextField(LINE_TEXT, line, Field.Store.YES)) // for text search
                        add(IntPoint(LINE_NUMBER, lineNumber))  // for range/filter
                        add(StringField(LINE_NUMBER, lineNumber.toString(), Field.Store.YES))
                        add(LongPoint(LAST_MODIFIED, file.lastModified())) // for timestamp filter
                        add(StoredField(LAST_MODIFIED, file.lastModified())) // for timestamp match
                    }
                    indexWriter.addDocument(document)
                }

                println("Indexed ${file.name} with ${file.readLines().size} lines")
            }
        }
    }

    override suspend fun search(file: File, queryString: String, clauseCount: Int): List<LineItem> {
        require(clauseCount <= IndexSearcher.getMaxClauseCount()) {
            println("clauseCount is greater than ${IndexSearcher.getMaxClauseCount()}")
        }

        val escaped = QueryParserBase.escape(queryString)
        val pathQuery = TermQuery(Term(ABSOLUTE_PATH, file.absolutePath))
        val textQuery = QueryParser(LINE_TEXT, analyzer)
            .apply { allowLeadingWildcard = true }
            .parse("*${escaped}*")
        val combinedQuery = BooleanQuery.Builder()
            .add(pathQuery, BooleanClause.Occur.MUST)
            .add(textQuery, BooleanClause.Occur.MUST)
            .build()

        val reader = DirectoryReader.open(fsDirectory)
        val searcher = IndexSearcher(reader)
        val hits = searcher.search(combinedQuery, clauseCount).scoreDocs
        val storedFields = searcher.storedFields()

        return hits.map { hit ->
            val doc = storedFields.document(hit.doc)
            println("Line ${doc[LINE_NUMBER]}: ${doc[LINE_TEXT]} for ${doc[ABSOLUTE_PATH]}")
            LineItem(doc[LINE_NUMBER].toInt(), doc[LINE_TEXT])
        }.also { reader.close() }
    }

    fun findAllOccurrences(line: String, word: String): List<IntRange> {
        val pattern = Regex(Regex.escape(word), RegexOption.IGNORE_CASE)
        return pattern.findAll(line).map { it.range }.toList()
    }

    companion object {
        const val ABSOLUTE_PATH = "ABSOLUTE_PATH"
        const val FILENAME = "FILENAME"
        const val LAST_MODIFIED = "LAST_MODIFIED"
        const val LINE_NUMBER = "LINE_NUMBER"
        const val LINE_TEXT = "LINE_TEXT"
    }
}