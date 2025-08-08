package app.brucehsieh.logneko

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import app.brucehsieh.logneko.presentation.MainScreenViewModel
import app.brucehsieh.logneko.presentation.composable.DesktopVerticalScroll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalCoroutinesApi
@Composable
@Preview
fun App(viewModel: MainScreenViewModel = koinViewModel()) {

    MaterialTheme {
        val lineItems = viewModel.lineItems.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = viewModel::flipShowFilePicker) {
                Text("Click me!")
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val listState = rememberLazyListState()
                SelectionContainer {
                    LazyColumn(state = listState) {
                        items(lineItems.itemCount) { index ->
                            val line = lineItems[index] ?: return@items
                            Row(horizontalArrangement = Arrangement.Start) {
                                DisableSelection {
                                    Text(
                                        text = "%,d".format(line.number),
                                        modifier = Modifier.width(64.dp),
                                        color = Color.Gray
                                    )
                                }
                                Text(text = line.text, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                DesktopVerticalScroll(
                    lazyListState = listState,
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        }
    }
}