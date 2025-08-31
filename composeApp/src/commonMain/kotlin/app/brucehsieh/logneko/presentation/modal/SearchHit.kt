package app.brucehsieh.logneko.presentation.modal

/**
 * Represents a single search result occurrence within the file, used for prev/next navigation and highlighting.
 *
 * Each [SearchHit] identifies:
 * - [lineNumber] — the 1-based number of the line containing the match.
 * - [occurrenceIndex] — the index of this match occurrence within that line's list of match ranges
 *   (0-based, e.g. the first, second, third occurrence on the same line).
 */
data class SearchHit(
    val lineNumber: Int,
    val occurrenceIndex: Int
)