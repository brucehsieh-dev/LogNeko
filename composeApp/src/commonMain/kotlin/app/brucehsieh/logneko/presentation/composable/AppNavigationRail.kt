package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppNavigationRail(
    isFilterEnabled: Boolean,
    onOpenFile: () -> Unit,
    onToggleFilterSheet: () -> Unit
) {
    NavigationRail {
        NavigationRailItem(
            selected = false,
            onClick = onOpenFile,
            icon = { Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = "Open file") },
            label = { Text("File") }
        )
        NavigationRailItem(
            selected = false,
            onClick = onToggleFilterSheet,
            icon = { Icon(imageVector = Icons.Outlined.Tune, contentDescription = "Set filters") },
            enabled = isFilterEnabled,
            label = { Text("Filter") }
        )
    }
}

@Preview
@Composable
fun AppNavigationRailPreview() {
    MaterialTheme {
        AppNavigationRail(
            isFilterEnabled = true,
            onOpenFile = {},
            onToggleFilterSheet = {}
        )
    }
}