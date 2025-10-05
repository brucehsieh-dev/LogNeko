package app.brucehsieh.logneko.presentation.modal.filter

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.brucehsieh.logneko.domain.filter.BooleanOp
import app.brucehsieh.logneko.domain.filter.FilterExpression
import app.brucehsieh.logneko.domain.modal.TermOp
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode.Companion.toFilterExpression
import java.util.UUID

/**
 * Editable, Compose-friendly version of [FilterExpression].
 *
 * - [FilterExpression] (domain) is immutable → good for evaluation.
 * - [FilterUiNode] (UI) is mutable + stable → good for live editing in Compose.
 *
 * Tree structure:
 * - [Group] = AND/OR group with children
 * - [Term] = single condition (leaf)
 *
 * Use [toFilterExpression] to convert back into an immutable domain tree.
 */
sealed interface FilterUiNode {
    val id: String

    /**
     * Editable group node.
     *
     * @property booleanOp Whether children are combined with AND or OR.
     * @property children Mutable list of child nodes (groups or terms).
     */
    @Stable
    data class Group(
        override val id: String = UUID.randomUUID().toString(),
        val children: SnapshotStateList<FilterUiNode> = mutableStateListOf()
    ) : FilterUiNode {

        constructor(
            id: String = UUID.randomUUID().toString(),
            booleanOp: BooleanOp = BooleanOp.AND,
            children: List<FilterUiNode> = emptyList()
        ) : this(id) {
            this.booleanOp = booleanOp
            this.children.addAll(children)
        }

        constructor(booleanOp: BooleanOp, children: List<FilterUiNode> = emptyList()) : this() {
            this.booleanOp = booleanOp
            this.children.addAll(children)
        }

        var booleanOp: BooleanOp by mutableStateOf(BooleanOp.AND)
    }

    /**
     * Editable leaf node (a single condition).
     *
     * @property text The string value to match (or regex if [regex] = true).
     * @property caseSensitive Match respecting case if true.
     * @property wholeWord Require word boundaries if true.
     * @property regex Interpret [text] as regex if true.
     * @property negated Negate the condition (NOT) if true.
     */
    @Stable
    data class Term(
        override val id: String = UUID.randomUUID().toString(),
    ) : FilterUiNode {
        var text: String by mutableStateOf("")
        var caseSensitive: Boolean by mutableStateOf(false)
        var wholeWord: Boolean by mutableStateOf(false)
        var regex: Boolean by mutableStateOf(false)
        var negated: Boolean by mutableStateOf(false)

        constructor(
            text: String = "",
            caseSensitive: Boolean = false,
            wholeWord: Boolean = false,
            regex: Boolean = false,
            negated: Boolean = false
        ) : this() {
            this.text = text
            this.caseSensitive = caseSensitive
            this.wholeWord = wholeWord
            this.regex = regex
            this.negated = negated
        }

        companion object {
            fun Term.addCondition(termOp: TermOp) {
                when (termOp) {
                    TermOp.CONTAINS -> negated = false
                    TermOp.DOES_NOT_CONTAIN -> negated = true
                }
            }
        }
    }

    companion object {
        /**
         * Convert a mutable [FilterUiNode] tree into an immutable [FilterExpression].
         *
         * Rules:
         * - Empty group → returns a harmless [FilterExpression.Term] with empty text.
         * - Group with one child → collapses to that child.
         * - Group with multiple children → produces a [FilterExpression.Group].
         */
        fun FilterUiNode.toFilterExpression(): FilterExpression = when (this) {
            is Term -> FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
            is Group -> when (children.size) {
                0 -> FilterExpression.Term("")
                1 -> children.first().toFilterExpression()
                else -> FilterExpression.Group(booleanOp, children.map { it.toFilterExpression() })
            }
        }
    }
}