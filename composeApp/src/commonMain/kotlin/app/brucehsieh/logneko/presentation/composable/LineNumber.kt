package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import app.brucehsieh.logneko.data.modal.LineItem

@Composable
fun LineNumber(lineItem: LineItem, modifier: Modifier = Modifier, fontSize: TextUnit, lineHeight: TextUnit) {
    DisableSelection {
        Text(
            text = lineItem.number.toString(),
            modifier = modifier,
            color = Color.Gray,
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }
}