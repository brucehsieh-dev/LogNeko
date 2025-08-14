package app.brucehsieh.logneko.data.modal

/**
 * Represents the mode of paging data.
 *
 * @property STREAMING The data is streamed from the file.
 * @property IN_MEMORY The data is loaded into memory.
 */
enum class PagingDataMode {
    STREAMING,
    IN_MEMORY
}