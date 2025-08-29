package app.brucehsieh.logneko.presentation

import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.presentation.modal.LineSource

data class UiState(
    val hasFile: Boolean = false,
    val lineSource: LineSource = LineSource.PAGING,
    val displayedLineItems: List<LineItem> = emptyList(),
    val filtering: Boolean = false,
    val filterQuery: String = "",
    val textQuerying: Boolean = false,
    val textQuery: String = "",
    val matchesByLine: Map<Int, List<IntRange>> = emptyMap()
)
