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
import app.brucehsieh.logneko.presentation.modal.LineSource
import app.brucehsieh.logneko.presentation.modal.SearchHit
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
import kotlinx.coroutines.flow.onEach
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

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _currentPlatformFile = MutableStateFlow<PlatformFile?>(null)
    private val _textQuery = MutableStateFlow("")

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
                    _uiState.update { it.copy(lineSource = LineSource.FULL_LIST) }
                }
                println("@@@@: full load took ${duration.inWholeMilliseconds} ms")
            }
            .launchIn(viewModelScope)

        fileLineRepository.allLines
            .onEach { lineItems ->
                if (lineItems.isNotEmpty()) {
                    _uiState.update { it.copy(displayedLineItems = lineItems) }
                }
            }
            .launchIn(viewModelScope)

        _textQuery
            .debounce(300)
            .distinctUntilChanged()
            .mapLatest { query ->
                _uiState.update { it.copy(textQuerying = true) }
                textSearchManager.findOccurrences(query)
            }
            .onEach { matches ->
                val matchesByLine = matches.associate { it.lineNumber to it.ranges }
                val searchHits = computeSearchHits(matchesByLine = matchesByLine)
                val activeMatchIndex = computeActiveIndex(searchHits, uiState.value.activeSearchHitIndex)

                _uiState.update {
                    it.copy(
                        textQuerying = false,
                        matchesByLine = matchesByLine,
                        searchHits = searchHits,
                        activeSearchHitIndex = activeMatchIndex
                    )
                }
            }
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

        viewModelScope.launch {
            val elapsed = measureTime {
                _uiState.update { it.copy(filtering = true, filterQuery = filterQuery) }
                val filtered = textSearchManager.filter(filterQuery)
                _uiState.update { it.copy(filtering = false, filterQuery = filterQuery, displayedLineItems = filtered) }
            }
            println("@@@@: searching took ${elapsed.inWholeMilliseconds} ms")
        }
    }

    fun onFilterClear() {
        _uiState.value = uiState.value.copy(filterQuery = "", displayedLineItems = fileLineRepository.allLines.value)
    }

    fun onTextQueryChange(textQuery: String) {
        _uiState.update { it.copy(textQuery = textQuery) }
        _textQuery.value = textQuery
    }

    /**
     * Get the appropriate pager.
     */
    private fun getLineItemPagingData(
        platformFile: PlatformFile,
        pagingDataMode: PagingDataMode
    ): Flow<PagingData<LineItem>> =
        when (getPlatform()) {
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

    fun nextMatch() = _uiState.update { s ->
        if (s.searchHits.isEmpty()) s
        else s.copy(activeSearchHitIndex = (s.activeSearchHitIndex + 1).mod(s.searchHits.size))
    }

    fun prevMatch() = _uiState.update { s ->
        if (s.searchHits.isEmpty()) s
        else s.copy(activeSearchHitIndex = (s.activeSearchHitIndex - 1).mod(s.searchHits.size))
    }

    /** Optional: jump to the first match on a specific line (e.g., from a gutter click). */
    fun focusLine(lineNumber: Int) = _uiState.update { s ->
        val idx = s.searchHits.indexOfFirst { it.lineNumber == lineNumber }
        if (idx >= 0) s.copy(activeSearchHitIndex = idx) else s
    }

    private fun computeSearchHits(
        matchesByLine: Map<Int, List<IntRange>>
    ): List<SearchHit> {
        if (matchesByLine.isEmpty()) return emptyList()

        val searchHits = ArrayList<SearchHit>()
        matchesByLine.iterator().forEach { (lineNumber, ranges) ->
            ranges.forEachIndexed { idx, _ ->
                searchHits += SearchHit(lineNumber = lineNumber, occurrenceIndex = idx)
            }
        }
        return searchHits
    }

    private fun computeActiveIndex(searchHits: List<SearchHit>, currentActiveIndex: Int) = if (searchHits.isEmpty()) {
        -1
    } else {
        currentActiveIndex.coerceIn(0, searchHits.lastIndex)
    }
}