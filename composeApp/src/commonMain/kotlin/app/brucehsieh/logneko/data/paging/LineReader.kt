package app.brucehsieh.logneko.data.paging

import app.brucehsieh.logneko.data.modal.LineItem

interface LineReader {
    suspend fun readLines(startLine: Int, count: Int): List<String>
    suspend fun readAll(progress: (readLines: Int) -> Unit = {}): List<LineItem>
}