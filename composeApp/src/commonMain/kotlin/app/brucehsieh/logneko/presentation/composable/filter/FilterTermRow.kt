package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterTermRow(
    root: FilterUiNode,
    filterUiNodeTerm: FilterUiNode.Term,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterTermField(
            root,
            filterUiNodeTerm,
            Modifier.weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = "Remove"
            )
        }
    }
}

@Preview
@Composable
fun FilterTermRowPreview() {
    Column {
        FilterTermRow(
            root = FilterUiNode.Group(),
            filterUiNodeTerm = FilterUiNode.Term(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                negated = false
            ),
            onRemove = {}
        )
        FilterTermRow(
            root = FilterUiNode.Group(),
            filterUiNodeTerm = FilterUiNode.Term(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                negated = true
            ),
            onRemove = {}
        )
    }
}