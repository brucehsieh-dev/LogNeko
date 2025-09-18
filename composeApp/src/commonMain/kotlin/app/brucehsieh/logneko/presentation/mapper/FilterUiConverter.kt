package app.brucehsieh.logneko.presentation.mapper

import app.brucehsieh.logneko.domain.filter.FilterExpression
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode

/**
 * Convert an immutable domain [FilterExpression] tree into a mutable, Compose-friendly
 * [FilterUiNode] tree for the visual editor.
 *
 * - Preserves the structure 1:1 (AND/OR groups stay as groups; no collapsing/expansion).
 * - Use this when you need to **load** an existing filter into the UI builder.
 * - See [FilterUiNode.toFilterExpression] for the reverse direction (UI → Domain).
 *
 * Example:
 *   val uiRoot: FilterUiNode = filterExpression.toUiFilterTree()
 *   // render uiRoot in the TreeFilterBuilder, let user edit, then convert back with toFilterExpression()
 */
fun FilterExpression.toUiFilterTree(): FilterUiNode = when (this) {
    is FilterExpression.Group -> FilterUiNode.Group(booleanOp = this.op).also { group ->
        // Recursively convert and append each child to the group's state list.
        children.forEach { child -> group.children += child.toUiFilterTree() }
    }

    is FilterExpression.Term -> FilterUiNode.Term(text, caseSensitive, wholeWord, regex, negated)
}