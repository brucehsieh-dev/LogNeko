package app.brucehsieh.logneko.presentation

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.LineSource
import app.brucehsieh.logneko.presentation.modal.SearchHit

data class UiState(
    val hasFile: Boolean = false,
    val lineSource: LineSource = LineSource.PAGING,
    val displayedLineItems: List<LineItem> = emptyList(),
    val filtering: Boolean = false,
    val filterQuery: String = "",
    val textQuerying: Boolean = false,
    val textQuery: String = "",
    val matchesByLine: Map<Int, List<IntRange>> = emptyMap(),
    val searchHits: List<SearchHit> = emptyList(),
    val activeSearchHitIndex: Int = -1,
    val fontSizeSp: TextUnit = 14.sp
)
