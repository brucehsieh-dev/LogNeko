package app.brucehsieh.logneko.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    activeMatch: Int,
    totalMatches: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        SearchBarDefaults.InputField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = { onSearch(searchQuery) },
            expanded = false,
            onExpandedChange = {},
            placeholder = { Text("Search in file") },
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable {
                            onSearchQueryChange("")
                            menuExpanded = false
                            onDismiss()
                        }
                    )

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.clickable {
                            menuExpanded = !menuExpanded
                        }
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = totalMatches > 0,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("$activeMatch / $totalMatches", style = MaterialTheme.typography.labelSmall)
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Previous",
                            modifier = Modifier.clickable(onClick = onPrevious)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Next",
                            modifier = Modifier.clickable(onClick = onNext)
                        )
                    }
                }
            },
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Whole word") },
                onClick = {
                    menuExpanded = false
                },
//                leadingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Match case") },
                onClick = {
                    menuExpanded = false
                },
//                leadingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Regular expression") },
                onClick = {
                    menuExpanded = false
                },
//                leadingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
            )
        }
    }
}

@Preview
@Composable
fun LogSearchBarPreview() {
    TextSearchBar(
        searchQuery = "Search Query",
        onSearchQueryChange = {},
        onSearch = {},
        activeMatch = 45,
        totalMatches = 100,
        onPrevious = {},
        onNext = {},
        onDismiss = {}
    )
}