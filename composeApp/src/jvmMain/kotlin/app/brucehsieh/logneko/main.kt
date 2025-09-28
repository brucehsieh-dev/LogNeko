package app.brucehsieh.logneko

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.brucehsieh.logneko.core.initializer.AppInitializer
import app.brucehsieh.logneko.presentation.composable.DesktopCompactScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.awt.Dimension

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
fun main() = application {
    AppInitializer()

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center)
        ),
        title = "LogNeko",
    ) {
        window.minimumSize = with(LocalDensity.current) {
            Dimension(
                150.dp.roundToPx(),
                100.dp.roundToPx()
            )
        }

        DesktopCompactScale(
            dpScale = 0.9f,
            enableMinimumInteractiveComponentEnforcement = false
        ) {
            App()
        }
    }
}