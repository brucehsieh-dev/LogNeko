package app.brucehsieh.logneko.domain.searching

import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.modal.Match

/**
 * In-memory file search implementation.
 *
 * @param lineItems The list of line items in memory to search.
 */
class InMemorySearcher(private var lineItems: List<LineItem> = emptyList()) {

    fun load(lineItems: List<LineItem>) {
        this.lineItems = lineItems
    }

    fun invalidate() {
        lineItems = emptyList()
    }

    /**
     * Fast literal substring search over already-loaded lines in memory.
     * Finding the needle in the haystack.
     *
     * @param queryString The string to search for, aka Needle.
     * @param ignoreCase Whether to ignore case when searching.
     * @param maxLines The maximum number of lines to return.
     */
    fun search(queryString: String, ignoreCase: Boolean = true, maxLines: Int = Int.MAX_VALUE): List<Match> {
        if (queryString.isEmpty()) return emptyList()
        val out = ArrayList<Match>()
        for (line in lineItems) {
            val text = line.text
            var from = 0
            var idx = text.indexOf(queryString, startIndex = from, ignoreCase = ignoreCase)
            if (idx >= 0) {
                val ranges = ArrayList<IntRange>(4)
                while (idx >= 0) {
                    ranges += idx until (idx + queryString.length)
                    from = idx + queryString.length
                    idx = text.indexOf(queryString, startIndex = from, ignoreCase = ignoreCase)
                }
                out += Match(line.number, text, ranges)
                if (out.size >= maxLines) break
            }
        }
        return out
    }

    fun filterLineItems(queryString: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<LineItem> {
        if (queryString.isEmpty()) return emptyList()
        val out = ArrayList<LineItem>()
        for (lineItem in lineItems) {
            if (lineItem.text.indexOf(queryString, startIndex = 0, ignoreCase = ignoreCase) >= 0) {
                out += lineItem
                if (out.size >= limit) break
            }
        }
        return out
    }

    fun filterLineNumbers(queryString: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<Int> {
        if (queryString.isEmpty()) return emptyList()
        val out = ArrayList<Int>()
        for (lineItem in lineItems) {
            if (lineItem.text.indexOf(queryString, startIndex = 0, ignoreCase = ignoreCase) >= 0) {
                out += lineItem.number
                if (out.size >= limit) break
            }
        }
        return out
    }

    fun anyMatch(query: String, ignoreCase: Boolean = true): Boolean {
        if (query.isEmpty()) return false
        for (lineItem in lineItems) {
            if (lineItem.text.indexOf(query, startIndex = 0, ignoreCase = ignoreCase) >= 0) return true
        }
        return false
    }
}