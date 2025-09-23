package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import app.brucehsieh.logneko.presentation.modal.filter.removeNode
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
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
    Column(modifier = modifier.offset(x = (level * 12).dp).background(Color.Gray.copy(alpha = level * 0.1f))) {
        Text(text = "level $level")

        Row {
            FilterBooleanField(filterUiNodeGroup)
            IconButton(onClick = { onAddGroup(filterUiNodeGroup.id) }) {
                Icon(
                    imageVector = Icons.Outlined.Layers,
                    contentDescription = "Add Group"
                )
            }
            IconButton(onClick = { onAddTerm(filterUiNodeGroup.id) }) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Term"
                )
            }
            if (level > 1) {
                IconButton(onClick = { onRemoveNode(filterUiNodeGroup.id) }) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Remove Group"
                    )
                }
            }
        }
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

                is FilterUiNode.Term -> FilterTermField(
                    root = root,
                    filterUiNodeTerm = child,
                    onRemove = { onRemoveNode(child.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun FilterGroupPreview() {
    val root = FilterUiNode.Group()
    FilterGroup(
        root = root,
        filterUiNodeGroup = root,
        level = 2,
        onAddGroup = { true },
        onAddTerm = { true },
        onRemoveNode = { root.removeNode(it) }
    )
}