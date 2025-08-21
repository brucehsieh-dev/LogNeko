package app.brucehsieh.logneko.presentation.modal

/**
 * Row type used by LazyColumn `contentType` for better slot reuse.
 * This separation reduces unnecessary recomposition and remeasure when placeholders are replaced by real items.
 *
 * [Placeholder]    Unloaded paging slot (no data yet).
 * [NumberTextLine] Loaded row with line number and text.
 */
enum class ItemType {
    Placeholder,
    NumberTextLine
}