package app.brucehsieh.logneko.presentation.composable

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

class TooltipIcon(
    private val imageVector: ImageVector,
    private val contentDescription: String
) : @Composable () -> Unit {

    @Suppress("ComposableNaming")
    @Composable
    override fun invoke() {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}