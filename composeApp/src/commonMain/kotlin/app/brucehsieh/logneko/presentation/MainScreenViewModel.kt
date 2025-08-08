package app.brucehsieh.logneko.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import app.brucehsieh.logneko.core.util.Platform
import app.brucehsieh.logneko.core.util.getPlatform
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
class MainScreenViewModel : ViewModel(), KoinComponent {

    private val fileLineRepository by inject<FileLineRepository>()

    private val _currentPlatformFile = MutableStateFlow<PlatformFile?>(null)
    private val _showFilePicker = MutableStateFlow(false)
    val showFilePicker = _showFilePicker

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
        .cachedIn(viewModelScope)

    fun flipShowFilePicker() {
        _showFilePicker.value = !showFilePicker.value

        if (showFilePicker.value) {
            launchFilePicker()
        }
    }

    private fun launchFilePicker() {
        viewModelScope.launch {
            _showFilePicker.value = false
            _currentPlatformFile.value = FileKit.openFilePicker() ?: return@launch
        }
    }
}