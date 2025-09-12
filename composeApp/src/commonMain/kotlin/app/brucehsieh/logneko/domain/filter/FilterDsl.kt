package app.brucehsieh.logneko.domain.filter

/**
 * Filter entry point.
 *
 * Example:
 *   val expr = filter {
 *     and {
 *       term("error")
 *       or {
 *         term("timeout")
 *         term("failed")
 *       }
 *       notTerm("debug")
 *     }
 *   }
 */
fun filter(block: FilterBuilderScope.() -> Unit) = FilterBuilderScope().apply(block).toFilterExpression()

/**
 * DSL scope that builds a [FilterExpression] tree.
 *
 * Supported:
 * - [term] and [notTerm] for leaves (auto-collected).
 * - [and]/[or] with block form for nested groups.
 * - [and]/[or] with vararg [FilterExpression] or plain [String] terms.
 */
class FilterBuilderScope {

    private val children = mutableListOf<FilterExpression>()

    /*
     * ------------------------- Leaves -------------------------
     */

    /**
     * Add a term (auto-collected).
     */
    fun term(
        text: String,
        caseSensitive: Boolean = false,
        wholeWord: Boolean = false,
        regex: Boolean = false,
        negated: Boolean = false
    ) {
        children += FilterExpression
            .Term(text = text, caseSensitive = caseSensitive, wholeWord = wholeWord, regex = regex, negated = negated)
    }

    /**
     * Add a NOT term (auto-collected).
     */
    fun notTerm(text: String, caseSensitive: Boolean = false, wholeWord: Boolean = false, regex: Boolean = false) {
        term(text, caseSensitive, wholeWord, regex, negated = true)
    }

    /*
     * ------------------------- Groups: block style -------------------------
     */

    /**
     * Add a nested AND group built from [block] (auto-collected).
     *
     * IMPORTANT: This uses the block's *raw children* to construct the group, so multiple items become an AND group
     * (as intended), and a single item collapses.
     */
    fun and(block: FilterBuilderScope.() -> Unit) {
        val nestedScope = FilterBuilderScope().apply(block)
        val list = nestedScope.extractChildren()
        children += when (list.size) {
            0 -> return
            1 -> list[0]
            else -> FilterExpression.Group(BooleanOp.AND, list)
        }
    }

    /**
     * Add a nested OR group built from [block] (auto-collected).
     *
     * IMPORTANT: Uses the block's *raw children* to ensure the group is OR, never accidentally AND due to
     * root-collapsing rules.
     */
    fun or(block: FilterBuilderScope.() -> Unit) {
        val nestedScope = FilterBuilderScope().apply(block)
        val list = nestedScope.extractChildren()
        children += when (list.size) {
            0 -> return
            1 -> list[0]
            else -> FilterExpression.Group(BooleanOp.OR, list)
        }
    }

    /*
     * ------------------------- Groups: vararg style -------------------------
     */

    /**
     * Add an AND group from explicit child nodes (collapses on 0/1 items).
     */
    fun and(vararg nodes: FilterExpression) = when (nodes.size) {
        0 -> Unit
        1 -> children += nodes[0]
        else -> children += FilterExpression.Group(BooleanOp.AND, nodes.toList())
    }

    /**
     * Add an OR group from explicit child nodes (collapses on 0/1 items).
     */
    fun or(vararg nodes: FilterExpression) = when (nodes.size) {
        0 -> Unit
        1 -> children += nodes[0]
        else -> children += FilterExpression.Group(BooleanOp.OR, nodes.toList())
    }

    /**
     * AND a list of strings (each becomes a simple term).
     */
    fun and(vararg texts: String) {
        if (texts.isEmpty()) return
        if (texts.size == 1) {
            term(texts[0]); return
        }
        children += FilterExpression.Group(
            BooleanOp.AND,
            texts.map { FilterExpression.Term(it) }
        )
    }

    /**
     * OR a list of strings (each becomes a simple term).
     */
    fun or(vararg texts: String) {
        if (texts.isEmpty()) return
        if (texts.size == 1) {
            term(texts[0]); return
        }
        children += FilterExpression.Group(
            BooleanOp.OR,
            texts.map { FilterExpression.Term(it) }
        )
    }

    /*
     * ------------------------- Finalization -------------------------
     */

    /**
     * Convert collected nodes to a single [FilterExpression].
     */
    internal fun toFilterExpression(): FilterExpression =
        when (children.size) {
            0 -> FilterExpression.Term("") // safe no-op
            1 -> children.first()
            else -> FilterExpression.Group(BooleanOp.AND, children.toList())
        }

    /**
     * Expose raw children for sibling scopes (used by and/or block builders).
     */
    private fun extractChildren(): List<FilterExpression> = children.toList()
}
