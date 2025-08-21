package app.brucehsieh.logneko.data.modal

import androidx.compose.runtime.Immutable

@Immutable
data class LineItem(
    val number: Int,
    val text: String
)