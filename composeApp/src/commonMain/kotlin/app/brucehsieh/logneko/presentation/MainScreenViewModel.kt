package app.brucehsieh.logneko.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import app.brucehsieh.logneko.core.util.FileHelper
import app.brucehsieh.logneko.core.util.Platform
import app.brucehsieh.logneko.core.util.defaultScope
import app.brucehsieh.logneko.core.util.getPlatform
import app.brucehsieh.logneko.core.util.indexingScope
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import app.brucehsieh.logneko.domain.searching.SearchEngine
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.time.measureTime

@ExperimentalCoroutinesApi
class MainScreenViewModel : ViewModel(), KoinComponent {

    private val fileLineRepository by inject<FileLineRepository>()
    private val searchEngine by inject<SearchEngine> { parametersOf(FileHelper.fsDirectory) }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private val _currentPlatformFile = MutableStateFlow<PlatformFile?>(null)
    private val _showFilePicker = MutableStateFlow(false)
    val showFilePicker = _showFilePicker.asStateFlow()

    val lineItems = _currentPlatformFile
        .filterNotNull()
        .flatMapLatest { platformFile ->
            when (getPlatform()) {
                Platform.ANDROID -> fileLineRepository
                    .getFileLinesPagedByContentUri(platformFile.absolutePath(), pageSize = 100)

                Platform.DESKTOP -> fileLineRepository
                    .getFileLinesPagedByPath(platformFile.absolutePath(), pageSize = 100)
            }
        }
        .cachedIn(defaultScope)

    init {
        _currentPlatformFile
            .filterNotNull()
            .mapLatest { platformFile ->
                _uiState.value = UiState(indexing = true)
                measureTime {
                    searchEngine.index(platformFile.file)
                }.also { println("@@@@: indexing took ${it.inWholeMilliseconds} ms") }
                _uiState.value = UiState(indexing = false)
            }
            .launchIn(indexingScope)
    }

    fun flipShowFilePicker() {
        _showFilePicker.value = !showFilePicker.value

        if (showFilePicker.value) {
            launchFilePicker()
        }
    }

    fun onSearch(queryString: String) {
        if (queryString.isBlank()) return
        if (uiState.value.indexing) return

        viewModelScope.launch {
            measureTime {
                withContext(Dispatchers.IO) {
                    _currentPlatformFile.value?.file?.let { file ->
                        _uiState.value = UiState(searching = true)
                        _uiState.value = UiState(
                            searching = false,
                            filteredLineItems = searchEngine.search(file, queryString, 100)
                        )
                    }
                }
            }.also { println("@@@@: searching took ${it.inWholeMilliseconds} ms") }
        }
    }

    fun onSearchClear() {
        _uiState.value = UiState()
    }

    private fun launchFilePicker() {
        viewModelScope.launch {
            _showFilePicker.value = false
            _currentPlatformFile.value = FileKit.openFilePicker() ?: return@launch
        }
    }
}