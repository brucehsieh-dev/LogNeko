package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.domain.filter.BooleanOp
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import org.jetbrains.compose.ui.tooling.preview.Preview

@ExperimentalMaterial3Api
@Composable
fun FilterBooleanField(
    filterUiNodeGroup: FilterUiNode.Group,
    modifier: Modifier = Modifier
) {
    val options = BooleanOp.entries.map { it.label }
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(initialText = filterUiNodeGroup.booleanOp.label)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textFieldState.text.toString(),
                onValueChange = {},
                modifier = Modifier.width(100.dp).menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            Text(text = " of the following is true")
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(option)
                        filterUiNodeGroup.booleanOp = BooleanOp.entries.find { it.label == option }!!
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun FilterBooleanFieldPreview() {
    FilterBooleanField(FilterUiNode.Group())
}