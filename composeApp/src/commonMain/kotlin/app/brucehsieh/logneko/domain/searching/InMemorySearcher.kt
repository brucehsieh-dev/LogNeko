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
     * Search all loaded lines for [textQuery].
     *
     * @param textQuery literal substring to find (no regex).
     * @param ignoreCase case-insensitive by default.
     * @param maxLines max number of *lines* to return (not occurrences).
     * @param allowOverlap when true, advances by 1 to capture overlapping hits.
     */
    fun search(
        textQuery: String,
        ignoreCase: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        allowOverlap: Boolean = true
    ): List<Match> {
        if (textQuery.isEmpty()) return emptyList()
        val out = ArrayList<Match>()
        for (line in lineItems) {
            val ranges = findAllRanges(line.text, textQuery, ignoreCase, allowOverlap)
            if (ranges.isNotEmpty()) {
                out += Match(line.number, ranges)
                if (out.size >= maxLines) break
            }
        }
        return out
    }

    fun filter(filterQuery: String, ignoreCase: Boolean = true, limit: Int = Int.MAX_VALUE): List<LineItem> {
        if (filterQuery.isEmpty()) return emptyList()
        val out = ArrayList<LineItem>()
        for (lineItem in lineItems) {
            if (lineItem.text.indexOf(filterQuery, startIndex = 0, ignoreCase = ignoreCase) >= 0) {
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

    private fun findAllRanges(
        haystack: String,
        needle: String,
        ignoreCase: Boolean,
        allowOverlap: Boolean
    ): List<IntRange> {
        val out = ArrayList<IntRange>()
        var start = 0
        val step = if (allowOverlap) 1 else needle.length
        while (true) {
            val idx = haystack.indexOf(needle, startIndex = start, ignoreCase = ignoreCase)
            if (idx < 0) break
            out += idx until (idx + needle.length)
            start = idx + step
        }
        return out
    }
}