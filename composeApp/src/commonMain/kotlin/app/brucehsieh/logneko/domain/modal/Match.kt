package app.brucehsieh.logneko.domain.modal

/**
 * A single line's search result.
 *
 * @param lineNumber 1-based line number.
 * @param ranges     Match spans as half-open ranges built with [start, endExclusive),
 *                   Overlaps are allowed and intentionally not merged.
 */
data class Match(
    val lineNumber: Int,
    val ranges: List<IntRange>
)