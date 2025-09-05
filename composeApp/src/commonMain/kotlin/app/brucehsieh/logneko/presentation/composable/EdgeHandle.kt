package app.brucehsieh.logneko.presentation.composable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EdgeHandle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        modifier = modifier.width(28.dp).height(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Preview
@Composable
fun EdgeHandlePreviewWithIcon() {
    MaterialTheme {
        Box(Modifier.fillMaxWidth()) {
            EdgeHandle(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = "Font size"
                )
            }
        }
    }
}

@Preview
@Composable
fun EdgeHandlePreviewWithText() {
    MaterialTheme {
        Box(Modifier.fillMaxWidth()) {
            EdgeHandle(onClick = {}) {
                Text(
                    text = "Aa",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}