package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.domain.filter.filter
import app.brucehsieh.logneko.presentation.composable.shape8dp
import app.brucehsieh.logneko.presentation.mapper.toUiFilterTree
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import app.brucehsieh.logneko.presentation.modal.filter.removeNode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterGroup(
    root: FilterUiNode.Group,
    filterUiNodeGroup: FilterUiNode.Group,
    level: Int,
    onAddGroup: (parentId: String) -> Boolean,
    onAddTerm: (parentId: String) -> Boolean,
    onRemoveNode: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val offsetDp = 12.dp
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth().padding(
            start = if (level == 1) 0.dp else offsetDp
        ),
        shape = shape8dp
    ) {
        Row {
            Column {
                FilterBooleanFieldRow(
                    filterUiNodeGroup = filterUiNodeGroup,
                    level = level,
                    onAddGroup = onAddGroup,
                    onAddTerm = onAddTerm,
                    onRemoveNode = onRemoveNode
                )
                filterUiNodeGroup.children.forEachIndexed { index, child ->
                    when (child) {
                        is FilterUiNode.Group -> FilterGroup(
                            root = root,
                            filterUiNodeGroup = child,
                            level = level + 1,
                            onAddGroup = onAddGroup,
                            onAddTerm = onAddTerm,
                            onRemoveNode = onRemoveNode
                        )

                        is FilterUiNode.Term -> FilterTermRow(
                            root = root,
                            filterUiNodeTerm = child,
                            onRemove = { onRemoveNode(child.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FilterGroupPreview() {
    val root = filter {
        and {
            term("abc")
            or {
                term("ball")
                term("duck", negated = true)
                and {
                    term("cat")
                    term("dog", negated = true)
                }
            }
        }
    }.toUiFilterTree() as FilterUiNode.Group

    Box(
        modifier = Modifier.padding(4.dp)
    ) {
        FilterGroup(
            root = root,
            filterUiNodeGroup = root,
            level = 1,
            onAddGroup = { true },
            onAddTerm = { true },
            onRemoveNode = { root.removeNode(it) }
        )
    }
}