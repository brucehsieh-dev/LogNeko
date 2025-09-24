package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DesktopCompactScale(
    dpScale: Float = 1f,
    spScale: Float = 1f,
    enableMinimumInteractiveComponentEnforcement: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = baseDensity.density * dpScale,
            fontScale = baseDensity.fontScale * spScale
        ),
        LocalMinimumInteractiveComponentEnforcement provides enableMinimumInteractiveComponentEnforcement
    ) {
        content()
    }
}