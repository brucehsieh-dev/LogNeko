package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterChipRow(
    filterQuery: String,
    onClear: () -> Unit
) {
    if (filterQuery.isNotEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            FlowRow {
                InputChip(
                    selected = true,
                    onClick = onClear,
                    label = { Text("Filter On") },
                    trailingIcon = { Icon(Icons.Outlined.Close, contentDescription = "Close") }
                )
            }
        }
    }
}

@Preview
@Composable
fun FilterChipRowPreview() {
    MaterialTheme {
        FilterChipRow(
            filterQuery = "Filter Query",
            onClear = {}
        )

    }
}