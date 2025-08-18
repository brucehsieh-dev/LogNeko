package app.brucehsieh.logneko.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.brucehsieh.logneko.core.util.FileHelper
import app.brucehsieh.logneko.core.util.Platform
import app.brucehsieh.logneko.core.util.defaultScope
import app.brucehsieh.logneko.core.util.getPlatform
import app.brucehsieh.logneko.core.util.indexingScope
import app.brucehsieh.logneko.data.modal.LineItem
import app.brucehsieh.logneko.data.modal.PagingDataMode
import app.brucehsieh.logneko.data.paging.LineReader
import app.brucehsieh.logneko.domain.manager.TextSearchManager
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import app.brucehsieh.logneko.domain.searching.SearchEngine
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
import kotlin.time.measureTime

@FlowPreview
@ExperimentalCoroutinesApi
class MainScreenViewModel : ViewModel(), KoinComponent {

    private val fileLineRepository by inject<FileLineRepository>()
    private val searchEngine by inject<SearchEngine> { parametersOf(FileHelper.fsDirectory) }
    private val textSearchManager by inject<TextSearchManager>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private val _currentPlatformFile = MutableStateFlow<PlatformFile?>(null)
    val currentPlatformFile = _currentPlatformFile.asStateFlow()
    private val _showFilePicker = MutableStateFlow(false)
    val showFilePicker = _showFilePicker.asStateFlow()

    private val _textQuery = MutableStateFlow("")

    val lineItems = combine(_currentPlatformFile, fileLineRepository.pagingDataMode, ::Pair)
        .transformLatest { (platformFile, pagingDataMode) ->
            platformFile ?: return@transformLatest

            val flow = getLineItemPagingData(platformFile, pagingDataMode).cachedIn(defaultScope)
            emitAll(flow)
        }

    init {
        _currentPlatformFile.indexFile()
        _currentPlatformFile.loadInMemory()

        _textQuery.debounce(300)
            .distinctUntilChanged()
            .mapLatest { query ->
                // TODO: Apply .trim() is needed
                _uiState.update { it.copy(textQuerying = true) }
                textSearchManager.findOccurrences(query)
            }
            .onEach { matches ->
                _uiState.update { it.copy(textQuerying = false, textQueryMatches = matches) }
            }
            .launchIn(viewModelScope)
    }

    fun flipShowFilePicker() {
        _showFilePicker.value = !showFilePicker.value

        if (showFilePicker.value) {
            launchFilePicker()
        }
    }

    fun onFilterApply(filterQuery: String) {
        if (filterQuery.isBlank()) return
        if (uiState.value.indexing) return

        viewModelScope.launch {
            measureTime {
                _currentPlatformFile.value?.file?.let { file ->
                    _uiState.value = UiState(filterQuery = filterQuery, filtering = true)
                    _uiState.value = UiState(
                        filtering = false,
                        filterQuery = filterQuery,
                        filteredLineItems = textSearchManager.filter(filterQuery)
//                            filteredLineItems = searchEngine.search(file, queryString)
                    )
                }
            }.also { println("@@@@: searching took ${it.inWholeMilliseconds} ms") }
        }
    }

    fun onFilterClear() {
        _uiState.value = uiState.value.copy(filterQuery = "")
    }

    fun onTextQueryChange(textQuery: String) {
        _uiState.value = uiState.value.copy(textQuery = textQuery)
        _textQuery.value = textQuery
    }

    private fun launchFilePicker() {
        viewModelScope.launch {
            _showFilePicker.value = false
            _currentPlatformFile.value = FileKit.openFilePicker() ?: return@launch
        }
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
                val lineReader = get<LineReader> { parametersOf(platformFile.absolutePath()) }
                when (pagingDataMode) {
                    PagingDataMode.STREAMING -> fileLineRepository.streamPager(lineReader, pageSize = 500)
                    PagingDataMode.IN_MEMORY -> fileLineRepository.memoryPager(lineReader, pageSize = 500)
                        .also { println("@@@@ IN MEMORY @@@@@") }
                }
            }

            Platform.ANDROID -> {
                fileLineRepository.getFileLinesPagedByContentUri(
                    platformFile.absolutePath(),
                    pageSize = 100
                )
            }
        }

    /**
     * Index the current file.
     */
    private fun Flow<PlatformFile?>.indexFile() = filterNotNull().mapLatest { platformFile ->
        _uiState.value = UiState(indexing = true)
        measureTime {
            searchEngine.index(platformFile.file)
        }.also { println("@@@@: indexing took ${it.inWholeMilliseconds} ms") }
        _uiState.value = UiState(indexing = false)
    }.launchIn(indexingScope)

    /**
     * Load the current file into memory.
     */
    private fun Flow<PlatformFile?>.loadInMemory() = filterNotNull().mapLatest { platformFile ->
        measureTime {
            val allLines = get<LineReader> { parametersOf(platformFile.absolutePath()) }.readAll { }
            fileLineRepository.fullLoaded(allLines)
        }.also { println("@@@@: full load took ${it.inWholeMilliseconds} ms") }
    }.launchIn(indexingScope)
}