package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.pointerInput
import app.brucehsieh.logneko.core.util.OperatingSystem

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ZoomableSurface(
    modifier: Modifier = Modifier,
    enableWheelZoom: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomReset: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            // Keyboard: Cmd/Ctrl + (= / - / 0)
            .onPreviewKeyEvent { event ->
                if (!OperatingSystem.isDesktop || event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                if (!keyZoomModifierPressed(event)) return@onPreviewKeyEvent false

                when (event.key) {
                    Key.Equals -> {
                        onZoomIn()
                        true
                    }

                    Key.Minus -> {
                        onZoomOut()
                        true
                    }

                    Key.Zero, Key.NumPad0 -> {
                        onZoomReset()
                        true
                    }

                    else -> false
                }
            }
            .pointerInput(enableWheelZoom) {
                if (!enableWheelZoom || !OperatingSystem.isDesktop) return@pointerInput

                awaitPointerEventScope {
                    while (true) {
                        val pointerEvent = awaitPointerEvent()
                        if (!pointerZoomModifierPressed(pointerEvent)) continue

                        val deltaY = pointerEvent.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                        if (deltaY == 0f) continue

                        if (deltaY > 0) onZoomIn() else onZoomOut()
                        pointerEvent.changes.forEach { it.consume() }
                    }
                }
            }
//            .onPointerEvent(PointerEventType.Scroll) { event ->
//                if (!enableWheelZoom || !OperatingSystem.isDesktop) return@onPointerEvent
//                if (!pointerZoomModifierPressed(event)) return@onPointerEvent
//
//                val deltaY = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
//                if (deltaY == 0f) return@onPointerEvent
//                if (deltaY > 0) onZoomIn() else onZoomOut()
//
//                event.changes.forEach { it.consume() }
//            }
    ) {
        content()
    }
}

private fun keyZoomModifierPressed(keyEvent: KeyEvent): Boolean {
    if (!OperatingSystem.isDesktop) return false
    return when (OperatingSystem.type()) {
        OperatingSystem.OperatingSystemType.MAC -> keyEvent.isMetaPressed
        OperatingSystem.OperatingSystemType.LINUX,
        OperatingSystem.OperatingSystemType.WINDOWS -> keyEvent.isCtrlPressed

        else -> false
    }
}

private fun pointerZoomModifierPressed(pointerEvent: PointerEvent): Boolean {
    if (!OperatingSystem.isDesktop) return false
    return when (OperatingSystem.type()) {
        OperatingSystem.OperatingSystemType.MAC -> pointerEvent.keyboardModifiers.isMetaPressed
        OperatingSystem.OperatingSystemType.LINUX,
        OperatingSystem.OperatingSystemType.WINDOWS -> pointerEvent.keyboardModifiers.isCtrlPressed

        else -> return false
    }
}