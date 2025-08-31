package app.brucehsieh.logneko.domain.usecase

import app.brucehsieh.logneko.domain.usecase.base.BaseSuspendUseCase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openFilePicker

/**
 * Opens a system file picker and returns the selected file or null if cancelled.
 */
class OpenFileUseCase : BaseSuspendUseCase<Unit, PlatformFile?>() {
    override suspend fun execute(param: Unit): PlatformFile? = FileKit.openFilePicker()
}