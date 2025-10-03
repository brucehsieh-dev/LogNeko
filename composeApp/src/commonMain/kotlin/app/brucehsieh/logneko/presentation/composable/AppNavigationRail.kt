package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined._123
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppNavigationRail(
    hasFileLoaded: Boolean,
    showSearchUi: Boolean,
    onOpenFileUi: () -> Unit,
    onSearchUi: () -> Unit,
    onFilterUi: () -> Unit,
    onJumpToLineUi: () -> Unit
) {
    NavigationRail {
        NavigationRailItem(
            selected = false,
            onClick = onOpenFileUi,
            icon = { Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = "Open file") },
            label = { Text("File") }
        )
        NavigationRailItem(
            selected = showSearchUi,
            onClick = onSearchUi,
            icon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search in file") },
            enabled = hasFileLoaded,
            label = { Text("Search") }
        )
        NavigationRailItem(
            selected = false,
            onClick = onFilterUi,
            icon = { Icon(imageVector = Icons.Outlined.Tune, contentDescription = "Set filters") },
            enabled = hasFileLoaded,
            label = { Text("Filter") }
        )
        NavigationRailItem(
            selected = false,
            onClick = onJumpToLineUi,
            icon = { Icon(imageVector = Icons.Outlined._123, contentDescription = "Jump to line") },
            enabled = hasFileLoaded,
            label = { Text("Teleport") }
        )
    }
}

@Preview
@Composable
fun AppNavigationRailPreview() {
    MaterialTheme {
        AppNavigationRail(
            hasFileLoaded = true,
            showSearchUi = true,
            onOpenFileUi = {},
            onSearchUi = {},
            onFilterUi = {},
            onJumpToLineUi = {}
        )
    }
}