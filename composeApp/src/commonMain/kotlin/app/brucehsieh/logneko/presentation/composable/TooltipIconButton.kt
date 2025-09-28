package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.brucehsieh.logneko.presentation.designsystem.SquareChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    tooltipIcon: TooltipIcon,
    onClick: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(text)
            }
        },
        state = rememberTooltipState(),
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            tooltipIcon()
        }
    }
}

