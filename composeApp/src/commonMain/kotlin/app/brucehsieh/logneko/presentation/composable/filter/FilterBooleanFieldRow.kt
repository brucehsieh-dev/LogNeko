package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode

@Composable
fun FilterBooleanFieldRow(
    filterUiNodeGroup: FilterUiNode.Group,
    level: Int,
    onAddGroup: (parentId: String) -> Boolean,
    onAddTerm: (parentId: String) -> Boolean,
    onRemoveNode: (id: String) -> Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterBooleanField(
            filterUiNodeGroup,
            Modifier.weight(1f)
        )
        IconButton(onClick = { onAddGroup(filterUiNodeGroup.id) }) {
            Icon(
                imageVector = Icons.Outlined.Layers,
                contentDescription = "Add Group"
            )
        }
        IconButton(onClick = { onAddTerm(filterUiNodeGroup.id) }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add Term"
            )
        }
        if (level > 1) {
            IconButton(onClick = { onRemoveNode(filterUiNodeGroup.id) }) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = "Remove Group"
                )
            }
        }
    }
}