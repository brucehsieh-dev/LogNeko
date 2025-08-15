package app.brucehsieh.logneko

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.brucehsieh.logneko.core.initializer.AppInitializer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterial3Api
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