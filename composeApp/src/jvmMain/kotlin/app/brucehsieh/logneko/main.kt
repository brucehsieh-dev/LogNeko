package app.brucehsieh.logneko

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.brucehsieh.logneko.core.initializer.AppInitializer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
fun main() = application {
    AppInitializer()

    Window(
        onCloseRequest = ::exitApplication,
        title = "LogNeko",
    ) {
        App()
    }
}