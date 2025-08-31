package app.brucehsieh.logneko.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.brucehsieh.logneko.core.util.Platform
import app.brucehsieh.logneko.core.util.getPlatform
import app.brucehsieh.logneko.data.CONTENT_URL
import app.brucehsieh.logneko.data.JVM_FILE
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.data.modal.PagingDataMode
import app.brucehsieh.logneko.data.paging.LineReader
import app.brucehsieh.logneko.domain.manager.TextSearchManager
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import app.brucehsieh.logneko.domain.searching.InMemorySearcher
import app.brucehsieh.logneko.presentation.modal.LineSource
import app.brucehsieh.logneko.presentation.search.SearchNavigator
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import kotlin.time.measureTime

@FlowPreview
@ExperimentalCoroutinesApi
class MainScreenViewModel : ViewModel(), KoinComponent {

    private val fileLineRepository by inject<FileLineRepository>()
    private val textSearchManager by inject<TextSearchManager>()
    private val searchNavigator by inject<SearchNavigator>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _currentPlatformFile = MutableStateFlow<PlatformFile?>(null)
    private val _textQuery = MutableStateFlow("")
    private val _filterQuery = MutableStateFlow("")

    val lineItems = combine(_currentPlatformFile, fileLineRepository.pagingDataMode, ::Pair)
        .transformLatest { (platformFile, pagingDataMode) ->
            platformFile ?: return@transformLatest
            emitAll(getLineItemPagingData(platformFile, pagingDataMode))
        }.cachedIn(viewModelScope)

    init {
        _currentPlatformFile
            .filterNotNull()
            .mapLatest { platformFile ->
                val duration = measureTime {

                    val qualifier = when (getPlatform()) {
                        Platform.ANDROID -> named(CONTENT_URL)
                        Platform.DESKTOP -> named(JVM_FILE)
                    }
                    val lineReader = get<LineReader>(qualifier) { parametersOf(platformFile.absolutePath()) }

                    // TODO: Hook progress into UI if desired
                    val all = lineReader.readAll { }

                    fileLineRepository.fullLoaded(all)
                    _uiState.update { it.copy(lineSource = LineSource.FULL_LIST, displayedLineItems = all) }
                }
                println("@@@@: full load took ${duration.inWholeMilliseconds} ms")
            }
            .launchIn(viewModelScope)

        _textQuery
            .debounce(300)
            .distinctUntilChanged()
            .mapLatest(::runTextSearch)
            .launchIn(viewModelScope)

        _filterQuery
            .mapLatest(::applyFilter)
            .launchIn(viewModelScope)
    }

    fun openFilePicker() {
        viewModelScope.launch {
            val picked = FileKit.openFilePicker() ?: return@launch
            _currentPlatformFile.value = picked
            _uiState.update { it.copy(hasFile = true) }
        }
    }

    fun onFilterApply(filterQuery: String) {
        if (filterQuery.isBlank()) return
        _filterQuery.value = filterQuery
    }

    fun onFilterClear() {
        _filterQuery.value = ""
    }

    fun onTextQueryChange(textQuery: String) {
        _uiState.update { it.copy(textQuery = textQuery) }
        _textQuery.value = textQuery
    }

    fun nextMatch() = _uiState.update { s ->
        searchNavigator.next()
        s.copy(activeSearchHitIndex = searchNavigator.activeSearchHitIndex)
    }

    fun prevMatch() = _uiState.update { s ->
        searchNavigator.prev()
        s.copy(activeSearchHitIndex = searchNavigator.activeSearchHitIndex)
    }

    /**
     * Get the appropriate pager.
     */
    private fun getLineItemPagingData(
        platformFile: PlatformFile,
        pagingDataMode: PagingDataMode
    ): Flow<PagingData<LineItem>> = when (getPlatform()) {
        Platform.DESKTOP -> {
            val lineReader = get<LineReader>(named(JVM_FILE)) {
                parametersOf(platformFile.absolutePath())
            }
            when (pagingDataMode) {
                PagingDataMode.STREAMING -> fileLineRepository.streamPager(lineReader, pageSize = 500)
                PagingDataMode.IN_MEMORY -> fileLineRepository.memoryPager(lineReader, pageSize = 1000)
            }
        }

        Platform.ANDROID -> {
            val lineReader = get<LineReader>(named(CONTENT_URL)) {
                parametersOf(platformFile.absolutePath())
            }
            when (pagingDataMode) {
                PagingDataMode.STREAMING -> fileLineRepository.streamPager(lineReader, pageSize = 200)
                PagingDataMode.IN_MEMORY -> fileLineRepository.memoryPager(lineReader, pageSize = 500)
            }
        }
    }

    /**
     * Find matches for [textQuery], update navigator + UI in one place.
     */
    private suspend fun runTextSearch(textQuery: String) {
        _uiState.update { it.copy(textQuerying = true) }
        val matches = textSearchManager.findOccurrences(textQuery)
        val matchesByLine = matches.associate { it.lineNumber to it.ranges }
        searchNavigator.update(matchesByLine)
        _uiState.update {
            it.copy(
                textQuerying = false,
                matchesByLine = matchesByLine,
                searchHits = searchNavigator.searchHits,
                activeSearchHitIndex = searchNavigator.activeSearchHitIndex
            )
        }
    }

    /**
     * Apply or clear a filter and reuse existing matches without recomputing.
     *
     * - Blank filter: restore full set, reload InMemorySearcher, and re-run the active text search (if any) to rebuild
     *   matches for the unfiltered list.
     * - Non-blank filter: show "filtering", compute filtered list, reload InMemorySearcher, prune existing matches to
     *   visible lines, update navigator + UI.
     */
    private suspend fun applyFilter(filterQuery: String) {
        // Clear filter → restore everything and (optionally) re-run the text search.
        if (filterQuery.isEmpty()) {
            get<InMemorySearcher>().load(fileLineRepository.allLines.value)

            if (uiState.value.textQuery.isNotEmpty()) {
                runTextSearch(uiState.value.textQuery)
            }

            _uiState.update {
                it.copy(filterQuery = "", displayedLineItems = fileLineRepository.allLines.value)
            }
            return
        }

        // Apply filter
        _uiState.update {
            it.copy(filtering = true, filterQuery = filterQuery)
        }

        val filtered = textSearchManager.filter(filterQuery)
        get<InMemorySearcher>().load(filtered)

        val prunedMatchesByLine = pruneMatchesByLine(uiState.value.matchesByLine, filtered)
        if (prunedMatchesByLine.isNotEmpty()) {
            searchNavigator.update(prunedMatchesByLine)
        }

        _uiState.update {
            it.copy(
                displayedLineItems = filtered,
                filtering = false,
                filterQuery = filterQuery,
                textQuerying = false,
                matchesByLine = prunedMatchesByLine,
                searchHits = searchNavigator.searchHits,
                activeSearchHitIndex = searchNavigator.activeSearchHitIndex
            )
        }
    }

    /**
     * Keep only matches whose line numbers are still visible after filtering.
     */
    private fun pruneMatchesByLine(
        matchesByLine: Map<Int, List<IntRange>>,
        visibleLineItems: List<LineItem>
    ): Map<Int, List<IntRange>> {
        if (matchesByLine.isEmpty() || visibleLineItems.isEmpty()) return emptyMap()
        val allowed = visibleLineItems.mapTo(HashSet(visibleLineItems.size)) { it.number }
        return matchesByLine.filterKeys { it in allowed }
    }
}