package app.brucehsieh.logneko

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import app.brucehsieh.logneko.presentation.MainScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalCoroutinesApi
@Composable
@Preview
fun App(viewModel: MainScreenViewModel = koinViewModel()) {

    MaterialTheme {
        val fileLines = viewModel.fileLines.collectAsLazyPagingItems()

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
            LazyColumn {
                items(fileLines.itemCount) { index ->
                    val line = fileLines[index] ?: return@items
                    Text(line)
                }
            }
        }
    }
}