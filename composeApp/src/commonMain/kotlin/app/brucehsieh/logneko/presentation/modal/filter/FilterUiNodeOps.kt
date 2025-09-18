package app.brucehsieh.logneko.presentation.modal.filter

import app.brucehsieh.logneko.domain.filter.BooleanOp

/**
 * Find a Group by id (including this group) in DFS way.
 */
fun FilterUiNode.Group.findGroup(groupId: String): FilterUiNode.Group? {
    if (id == groupId) return this
    for (node in children) {
        if (node is FilterUiNode.Group) node.findGroup(groupId)?.let { return it }
    }
    return null
}

/**
 * Find the parent Group that directly contains [childId] in DFS way.
 */
fun FilterUiNode.Group.findParentGroup(childId: String): FilterUiNode.Group? {
    for (node in children) {
        if (node.id == childId) return this
        if (node is FilterUiNode.Group) node.findParentGroup(childId)?.let { return it }
    }
    return null
}

/**
 * Find a Term by id (including this group) in DFS way.
 */
fun FilterUiNode.Group.findTerm(termId: String): FilterUiNode.Term? {
    children.forEach { node ->
        when (node) {
            is FilterUiNode.Group -> node.findTerm(termId)?.let { return it }
            is FilterUiNode.Term -> if (node.id == termId) return node
        }
    }
    return null
}

/**
 * Add a new Group under the parent with [parentGroupId]. Returns the created Group or null.
 */
fun FilterUiNode.Group.addGroupUnder(
    parentGroupId: String,
    booleanOp: BooleanOp,
    block: FilterUiNode.Group.() -> Unit
): FilterUiNode.Group? = findGroup(parentGroupId)?.addGroup(booleanOp, block)

/**
 * Add a new Term under the parent with [parentGroupId]. Returns the created Term or null.
 */
fun FilterUiNode.Group.addTermUnder(
    parentGroupId: String,
    block: FilterUiNode.Term.() -> Unit = {}
): FilterUiNode.Term? = findGroup(parentGroupId)?.addTerm(block)

/**
 * Remove a node (Group or Term) by id.
 * Returns true on success. The root group is never removed.
 */
fun FilterUiNode.Group.removeNode(nodeId: String): Boolean {
    if (id == nodeId) return false
    val parent = findParentGroup(nodeId) ?: return false
    val index = parent.children.indexOfFirst { it.id == nodeId }
    return if (index >= 0) {
        parent.children.removeAt(index)
        true
    } else false
}

/**
 * Create and append a nested [FilterUiNode.Group], then return it.
 * Callers can chain edits:
 *   group.addGroup { booleanOp = OR }.addTerm { text = "error" }
 */
fun FilterUiNode.Group.addGroup(booleanOp: BooleanOp, block: FilterUiNode.Group.() -> Unit): FilterUiNode.Group =
    FilterUiNode.Group(booleanOp = booleanOp).apply(block).also { children.add(it) }

/**
 * Create and append a nested [Term], then return it.
 * Example:
 *   group.addTerm { text = "timeout"; negated = true }
 */
fun FilterUiNode.Group.addTerm(block: FilterUiNode.Term.() -> Unit): FilterUiNode.Term =
    FilterUiNode.Term().apply(block).also { children.add(it) }

/**
 * Append an existing child node instance (useful for moves/rehydration).
 * Returns true if the list changed.
 */
fun FilterUiNode.Group.add(node: FilterUiNode): Boolean = children.add(node)

/**
 * Update any fields of a Term by [termId] via [mutate].
 *
 * Usage:
 *   updateTerm(root, id) { negated = true; regex = true }
 *
 * @return true if the term was found and mutated; false otherwise.
 */
fun FilterUiNode.Group.updateTerm(termId: String, mutate: FilterUiNode.Term.() -> Unit): Boolean {
    val term = findTerm(termId) ?: return false
    term.mutate()
    return true
}