package app.brucehsieh.logneko.presentation.modal.filter

import androidx.compose.runtime.mutableStateListOf
import app.brucehsieh.logneko.domain.filter.BooleanOp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterUiNodeOpsTest {

    @Test
    fun findGroup() {
        // Arrange
        val group1 = FilterUiNode.Group(id = "group1", booleanOp = BooleanOp.OR, children = mutableStateListOf(child2))
        val root =
            FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND, children = mutableStateListOf(child1, group1))
        val fakeGroupId = "fakeGroupId"

        // Act
        val rootGroup = root.findGroup(root.id)
        val foundGroup1 = root.findGroup(group1.id)
        val foundGroupX = root.findGroup(fakeGroupId)

        // Assert
        assertNotNull(rootGroup)
        assertNotNull(foundGroup1)
        assertNull(foundGroupX)
        assertTrue { rootGroup is FilterUiNode.Group }
        assertTrue { foundGroup1 is FilterUiNode.Group }
        assertEquals(root.id, rootGroup?.id)
        assertEquals(group1.id, foundGroup1?.id)
    }

    @Test
    fun findParentGroup() {
        // Arrange
        val group1 = FilterUiNode.Group(id = "group1", booleanOp = BooleanOp.OR, children = mutableStateListOf(child2))
        val root =
            FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND, children = mutableStateListOf(child1, group1))

        // Act
        val parentOfChild1 = root.findParentGroup(child1.id)
        val parentOfChild2 = root.findParentGroup(child2.id)
        val parentOfChild3 = root.findParentGroup(child3.id)

        // Assert
        assertNotNull(parentOfChild1)
        assertNotNull(parentOfChild2)
        assertNull(parentOfChild3)
        assertTrue { parentOfChild1 is FilterUiNode.Group }
        assertTrue { parentOfChild2 is FilterUiNode.Group }
        assertEquals(root.id, parentOfChild1?.id)
        assertEquals(group1.id, parentOfChild2?.id)
    }

    @Test
    fun findTerm() {
        // Arrange
        val group1 = FilterUiNode.Group(id = "group1", booleanOp = BooleanOp.OR, children = mutableStateListOf(child2))
        val root =
            FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND, children = mutableStateListOf(child1, group1))

        // Act
        val foundTerm1 = root.findTerm(child1.id)
        val foundTerm2 = root.findTerm(child2.id)

        // Assert
        assertNotNull(foundTerm1)
        assertNotNull(foundTerm2)
        assertTrue { foundTerm1 is FilterUiNode.Term }
        assertTrue { foundTerm2 is FilterUiNode.Term }
        assertEquals(child1.id, foundTerm1?.id)
        assertEquals(child2.id, foundTerm2?.id)
    }

    @Test
    fun addGroup() {
        // Arrange
        val root = FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND)

        // Act
        val addedGroup = root.addGroup(BooleanOp.OR) {
            addTerm { text = "error" }
            addTerm { text = "timeout" }
        }

        // Assert
        assertNotNull(addedGroup)
        assertTrue { root.children.contains(addedGroup) }
        assertTrue { addedGroup.children.size == 2 }
        assertTrue { (root.children.first() as FilterUiNode.Group).children.contains(addedGroup.children.first()) }
        assertTrue { (root.children.first() as FilterUiNode.Group).children.contains(addedGroup.children.last()) }
    }

    @Test
    fun addTerm() {
        // Arrange
        val group =
            FilterUiNode.Group(id = "group", booleanOp = BooleanOp.OR, children = mutableStateListOf(child1, child2))
        val root = FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND, children = mutableStateListOf(group))

        // Act
        val addedTerm = group.addTerm { text = "error" }

        // Assert
        assertNotNull(addedTerm)
        assertTrue { group.children.contains(addedTerm) }
        assertTrue { (root.children.first() as FilterUiNode.Group).children.contains(addedTerm) }
    }

    @Test
    fun addGroupUnder() {
        // Arrange
        val root = FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND)

        // Act
        val addedGroup = root.addGroupUnder(root.id, BooleanOp.OR) {
            addTerm { text = "error" }
            addTerm { text = "timeout" }
        }

        // Assert
        assertNotNull(addedGroup)
        assertTrue { root.children.contains(addedGroup!!) }
        assertTrue { addedGroup!!.children.size == 2 }
    }

    @Test
    fun addTermUnder() {
        // Arrange
        val root = FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND)

        // Act
        val addedTerm = root.addTermUnder(root.id) {
            text = "error"
            negated = true
        }

        // Assert
        assertNotNull(addedTerm)
        assertTrue { root.children.first() == addedTerm }
    }

    @Test
    fun removeNode() {
        // Arrange
        val group1 = FilterUiNode.Group(id = "group1", booleanOp = BooleanOp.OR, children = mutableStateListOf(child1))
        val group2 = FilterUiNode.Group(id = "group2", booleanOp = BooleanOp.OR, children = mutableStateListOf(child2))
        val group3 = FilterUiNode.Group(id = "group3", booleanOp = BooleanOp.OR, children = mutableStateListOf(child3))
        val root = FilterUiNode.Group(
            id = "root",
            booleanOp = BooleanOp.AND,
            children = mutableStateListOf(group1, group2, group3)
        )

        // Act
        val removedChild1 = root.removeNode(child1.id)
        val removedGroup3 = root.removeNode(group3.id)
        val removedFake = root.removeNode("fake")

        // Assert
        assertTrue { removedChild1 }
        assertTrue { removedGroup3 }
        assertFalse { removedFake }
        assertTrue { root.children.size == 2 }
        assertTrue { root.children.contains(group1) }
        assertTrue { root.children.contains(group2) }
        assertTrue { !root.children.contains(group3) }
        assertTrue { group1.children.isEmpty() }
        assertTrue { group2.children.size == 1 }
        assertTrue { group2.children.contains(child2) }
    }

    @Test
    fun updateTerm() {
        // Arrange
        val root = FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND, children = mutableStateListOf(child1))

        // Act
        val updated = root.updateTerm(child1.id) {
            negated = true
            regex = true
        }

        // Assert
        assertTrue { updated }
        assertTrue { child1.negated }
        assertTrue { child1.regex }
        assertFalse { child1.wholeWord }
        assertFalse { child1.caseSensitive }
    }

    @Test
    fun add() {
        // Arrange
        val root = FilterUiNode.Group(id = "root", booleanOp = BooleanOp.AND, children = mutableStateListOf(child1))

        // Act
        val added = root.add(child2)

        // Assert
        assertTrue { added }
        assertTrue { root.children.contains(child2) }
    }

    companion object {
        val child1 = FilterUiNode.Term(text = "child no.1")
        val child2 = FilterUiNode.Term(text = "child no.2")
        val child3 = FilterUiNode.Term(text = "child no.3")
    }
}