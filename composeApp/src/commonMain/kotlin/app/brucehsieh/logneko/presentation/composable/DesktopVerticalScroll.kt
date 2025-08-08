package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Vertical scroll bar for Desktop only.
 */
@Composable
expect fun DesktopVerticalScroll(lazyListState: LazyListState, modifier: Modifier = Modifier)