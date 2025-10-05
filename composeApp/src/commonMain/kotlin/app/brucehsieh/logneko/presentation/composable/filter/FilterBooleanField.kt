package app.brucehsieh.logneko.presentation.composable.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.domain.filter.BooleanOp
import app.brucehsieh.logneko.presentation.designsystem.HorSpace8dp
import app.brucehsieh.logneko.presentation.designsystem.RetangleChip
import app.brucehsieh.logneko.presentation.composable.shape8dp
import app.brucehsieh.logneko.presentation.modal.filter.FilterUiNode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterBooleanField(
    filterUiNodeGroup: FilterUiNode.Group,
    modifier: Modifier = Modifier
) {
    val fieldHeight = 48.dp
    val booleanOps = BooleanOp.entries

    Surface(
        modifier = modifier.height(fieldHeight),
        shape = shape8dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Match",
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            HorSpace8dp()
            SingleChoiceSegmentedButtonRow(space = 0.dp) {
                booleanOps.forEachIndexed { index, booleanOp ->
                    RetangleChip(
                        text = booleanOp.label,
                        selected = filterUiNodeGroup.booleanOp == booleanOp,
                        onClick = {
                            filterUiNodeGroup.booleanOp = booleanOps.first { it == booleanOp }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBooleanFieldPreview() {
    Column {
        FilterBooleanField(FilterUiNode.Group())
        FilterBooleanField(FilterUiNode.Group(booleanOp = BooleanOp.OR))
    }
}