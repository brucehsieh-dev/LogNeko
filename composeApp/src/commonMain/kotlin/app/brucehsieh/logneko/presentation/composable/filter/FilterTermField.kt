package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import app.brucehsieh.logneko.presentation.modal.filter.updateTerm
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterTermField(
    root: FilterUiNode,
    filterUiNodeTerm: FilterUiNode.Term,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Contains")
        Spacer(Modifier.width(8.dp))
        OutlinedTextField(
            value = filterUiNodeTerm.text,
            onValueChange = {
                (root as FilterUiNode.Group).updateTerm(filterUiNodeTerm.id) { text = it }
            },
            modifier = modifier,
            singleLine = true
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Remove"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterTermFieldPreview() {
    FilterTermField(
        root = FilterUiNode.Term(text = "abc"),
        filterUiNodeTerm = FilterUiNode.Term(text = "abc"),
        onRemove = {}
    )
}