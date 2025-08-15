package app.brucehsieh.logneko.presentation

import app.brucehsieh.logneko.data.modal.LineItem

data class UiState(
    val indexing: Boolean = false,
    val searching: Boolean = false,
    val queryString: String = "",
    val filteredLineItems: List<LineItem> = emptyList()
)
