package app.brucehsieh.logneko.presentation

import app.brucehsieh.logneko.data.modal.LineItem

data class UiState(
    val filtering: Boolean = false,
    val filterQuery: String = "",
    val filteredLineItems: List<LineItem> = emptyList(),
    val textQuerying: Boolean = false,
    val textQuery: String = "",
    val matchesByLine: Map<Int, List<IntRange>> = emptyMap()
)
