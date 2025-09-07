package app.brucehsieh.logneko.domain.filter

/**
 * Represents a filter expression for log lines.
 * A filter can be a single Term (like "contains 'error'") or a Group of terms combined with AND/OR.
 */
sealed interface FilterExpr {

    /**
     * A single condition applied to a line of text.
     *
     * @param text          The raw text to match
     * @param caseSensitive If true, match must respect case
     * @param wholeWord     If true, match must be a whole word
     * @param regex         If true, treat text as a regex
     * @param negated       If true, result is inverted (NOT)
     */
    data class Term(
        val text: String,
        val caseSensitive: Boolean = false,
        val wholeWord: Boolean = false,
        val regex: Boolean = false,
        val negated: Boolean = false
    ) : FilterExpr

    /**
     * A group of child filters combined together.
     * Example: (A AND B) OR C
     *
     * @param op       The operator to apply to the children
     * @param children The sub-expressions
     */
    data class Group(
        val op: BooleanOp,
        val children: List<FilterExpr>
    ) : FilterExpr
}


/**
 * Defines the operators we support for groups.
 */
enum class BooleanOp { AND, OR }