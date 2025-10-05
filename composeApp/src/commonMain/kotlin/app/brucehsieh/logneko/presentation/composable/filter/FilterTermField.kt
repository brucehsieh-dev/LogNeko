package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.domain.modal.TermOp
import app.brucehsieh.logneko.presentation.designsystem.HorSpace8dp
import app.brucehsieh.logneko.presentation.composable.shape8dp
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode.Term.Companion.addCondition
import app.brucehsieh.logneko.presentation.modal.filter.updateTerm
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterTermField(
    root: FilterUiNode,
    filterUiNodeTerm: FilterUiNode.Term,
    modifier: Modifier = Modifier
) {
    // Lock the height of the text field to avoid height jumps while typing
    val fieldHeight = 48.dp
    val selectedTermOp = when {
        filterUiNodeTerm.negated -> TermOp.DOES_NOT_CONTAIN
        else -> TermOp.CONTAINS
    }
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.height(fieldHeight),
        shape = shape8dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "options",
                    modifier = Modifier.clickable {
                        menuExpanded = !menuExpanded
                    }
                )
                Text(
                    text = selectedTermOp.label,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            HorSpace8dp()
            BasicTextField(
                value = filterUiNodeTerm.text,
                onValueChange = {
                    (root as FilterUiNode.Group).updateTerm(filterUiNodeTerm.id) {
                        text = it

                        when (it) {
                            TermOp.CONTAINS.label -> negated = false
                            TermOp.DOES_NOT_CONTAIN.label -> negated = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                singleLine = true,
                interactionSource = remember { MutableInteractionSource() }
            )

        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            TermOp.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label, style = MaterialTheme.typography.titleMedium) },
                    onClick = {
                        menuExpanded = false

                        (root as FilterUiNode.Group).updateTerm(filterUiNodeTerm.id) {
                            text = filterUiNodeTerm.text
                            addCondition(option)
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterTermFieldPreview() {
    Column {
        FilterTermField(
            root = FilterUiNode.Term(),
            filterUiNodeTerm = FilterUiNode.Term(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                negated = false
            )
        )
        FilterTermField(
            root = FilterUiNode.Term(),
            filterUiNodeTerm = FilterUiNode.Term(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                negated = true
            )
        )
    }
}