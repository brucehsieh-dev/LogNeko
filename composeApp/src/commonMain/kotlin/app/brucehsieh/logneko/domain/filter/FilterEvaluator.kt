package app.brucehsieh.logneko.domain.filter

import app.brucehsieh.logneko.data.modal.LineItem

/**
 * Evaluates whether log lines match a given filter expression.
 */
object FilterEvaluator {

    /**
     * Check if a single line matches the given expression.
     */
    fun matches(lineItem: LineItem, filterExpr: FilterExpr): Boolean =
        when (filterExpr) {
            // Evaluate one condition
            is FilterExpr.Term -> {
                val result = matchTerm(lineItem.text, filterExpr)
                if (filterExpr.negated) !result else result
            }

            is FilterExpr.Group -> when (filterExpr.op) {
                BooleanOp.AND -> filterExpr.children.all { matches(lineItem, it) } // All must match
                BooleanOp.OR -> filterExpr.children.any { matches(lineItem, it) } // At least one must match
            }
        }

    /**
     * Check if the text matches a Term with the given flags.
     */
    private fun matchTerm(text: String, term: FilterExpr.Term): Boolean {
        if (term.text.isEmpty()) return false

        // Normalize case if not case sensitive
        val haystack = if (term.caseSensitive) text else text.lowercase()
        val needle = if (term.caseSensitive) term.text else term.text.lowercase()

        return when {
            // Regex mode
            term.regex -> runCatching { Regex(needle).containsMatchIn(haystack) }.getOrDefault(false)
            // Whole word mode
            term.wholeWord -> containsWholeWord(haystack, needle)
            // Simple substring
            else -> haystack.contains(needle)
        }
    }

    /**
     * Whole word check (uses \b word boundaries).
     */
    private fun containsWholeWord(haystack: String, word: String): Boolean {
        val pattern = "\\b${Regex.escape(word)}\\b".toRegex()
        return pattern.containsMatchIn(haystack)
    }
}