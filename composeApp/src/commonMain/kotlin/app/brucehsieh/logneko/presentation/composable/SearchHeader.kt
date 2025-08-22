package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        TextSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onQueryChange,
            onSearch = { /* handled in ViewModel */ }
        )
    }
}

@Preview
@Composable
fun SearchHeaderPreview() {
    MaterialTheme {
        SearchHeader(
            searchQuery = "Search Query",
            onQueryChange = {}
        )
    }
}