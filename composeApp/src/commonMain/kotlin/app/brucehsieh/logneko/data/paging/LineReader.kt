package app.brucehsieh.logneko.data.paging

interface LineReader {
    suspend fun readLines(startLine: Int, count: Int): List<String>
    suspend fun readAll(progress: (readLines: Int) -> Unit = {}): List<String>
}