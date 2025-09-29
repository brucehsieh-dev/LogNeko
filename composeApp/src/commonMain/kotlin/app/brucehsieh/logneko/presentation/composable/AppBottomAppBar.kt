package app.brucehsieh.logneko.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined._123
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.brucehsieh.logneko.presentation.designsystem.TooltipSquareChip
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomAppBar(
    hasFileLoaded: Boolean,
    showSearchUi: Boolean,
    onOpenFileUi: () -> Unit,
    onSearchUi: () -> Unit,
    onJumpToLineUi: () -> Unit,
    onFilterUi: () -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = hasFileLoaded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                TooltipSquareChip(
                    tooltipText = "Open file",
                    chipIcon = Icons.Outlined.FileOpen,
                    selected = false,
                    onClick = onOpenFileUi
                )
            }

            TooltipSquareChip(
                tooltipText = "Jump to line",
                chipIcon = Icons.Outlined._123,
                enabled = hasFileLoaded,
                selected = false,
                onClick = onJumpToLineUi
            )

            TooltipSquareChip(
                tooltipText = "Set filters",
                chipIcon = Icons.Outlined.Tune,
                enabled = hasFileLoaded,
                selected = false,
                onClick = onFilterUi
            )

            TooltipSquareChip(
                tooltipText = "Search",
                chipIcon = Icons.Outlined.Search,
                enabled = hasFileLoaded,
                selected = showSearchUi,
                onClick = onSearchUi
            )

            AnimatedVisibility(
                visible = !hasFileLoaded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                TooltipSquareChip(
                    tooltipText = "Open file",
                    chipIcon = Icons.Outlined.FileOpen,
                    selected = false,
                    onClick = onOpenFileUi
                )
            }
        }
    }
}

@Preview
@Composable
fun AppBottomAppBarPreview() {
    AppBottomAppBar(
        hasFileLoaded = true,
        showSearchUi = false,
        onOpenFileUi = {},
        onSearchUi = {},
        onJumpToLineUi = {},
        onFilterUi = {}
    )
}