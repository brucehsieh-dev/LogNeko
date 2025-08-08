package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun DesktopVerticalScroll(lazyListState: LazyListState, modifier: Modifier) {
    VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(lazyListState)
    )
}