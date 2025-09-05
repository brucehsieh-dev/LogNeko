package app.brucehsieh.logneko.presentation.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.brucehsieh.logneko.data.DEFAULT_FONT_MAX_SIZE
import app.brucehsieh.logneko.data.DEFAULT_FONT_MIN_SIZE
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun EdgeFontSizeAdjuster(
    value: Float,
    onChange: (Float) -> Unit,
    onChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    min: Float = DEFAULT_FONT_MIN_SIZE,
    max: Float = DEFAULT_FONT_MAX_SIZE,
    step: Float = 0.5f,
    autoHideMs: Long = 1500,
    showInitially: Boolean = false
) {
    var expanded by remember { mutableStateOf(showInitially) }
    var lastInteractionTs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Auto-hide: simple debounce on [value] or open/close state
    LaunchedEffect(expanded, value) {
        if (expanded) {
            val start = System.currentTimeMillis()
            lastInteractionTs = start
            delay(autoHideMs)
            if (expanded && (System.currentTimeMillis() - lastInteractionTs) >= autoHideMs) {
                expanded = false
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        EdgeHandle(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                expanded = true
                lastInteractionTs = System.currentTimeMillis()
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.TextFields,
                contentDescription = "Font size"
            )
        }

        // Sliding panel
        AnimatedVisibility(
            visible = expanded,
            enter = slideInHorizontally(animationSpec = tween(160)) { it } + fadeIn(tween(120)),
            exit = slideOutHorizontally(animationSpec = tween(160)) { it } + fadeOut(tween(120)),
            modifier = Modifier.offset(x = (-28).dp).align(Alignment.BottomEnd)
        ) {
            Slider(
                enabled = expanded,
                value = value.coerceIn(min, max),
                onValueChange = {
                    onChange(it.coerceIn(min, max))
                    lastInteractionTs = System.currentTimeMillis()
                },
                onValueChangeFinished = {
                    onChangeFinished()
                },
                valueRange = min..max,
                steps = ((max - min) / step).roundToInt() - 1,
                modifier = Modifier.width(maxWidth * 0.35f),
            )
        }
    }
}