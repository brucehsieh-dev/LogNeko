package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun AppBottomAppBar(
    isFilterEnabled: Boolean,
    onOpenFile: () -> Unit,
    onToggleFilterSheet: () -> Unit
) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = onToggleFilterSheet,
                enabled = isFilterEnabled
            ) {
                Icon(imageVector = Icons.Outlined.Tune, contentDescription = "Set filters")
            }
            IconButton(onClick = onOpenFile) {
                Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = "Open file")
            }
        }
    )
}