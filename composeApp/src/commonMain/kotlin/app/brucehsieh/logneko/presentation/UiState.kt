package app.brucehsieh.logneko.presentation

import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.domain.modal.Match

data class UiState(
    val indexing: Boolean = false,
    val filtering: Boolean = false,
    val filterQuery: String = "",
    val filteredLineItems: List<LineItem> = emptyList(),
    val textQuerying: Boolean = false,
    val textQuery: String = "",
    val textQueryMatches: List<Match> = emptyList()
)
