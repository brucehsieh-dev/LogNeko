package app.brucehsieh.logneko.domain.modal

/**
 * A single line that contains one or more literal matches of the query.
 *
 * @param lineNumber The line number in the file.
 * @param lineText The text of the line.
 * @param ranges A list of ranges that match the query. [start, endExclusive) for each occurrence
 */
data class Match(
    val lineNumber: Int,
    val lineText: String,
    val ranges: List<IntRange>
)