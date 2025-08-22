package app.brucehsieh.logneko

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import app.brucehsieh.logneko.presentation.MainScreenViewModel
import app.brucehsieh.logneko.presentation.composable.AppNavigationRail
import app.brucehsieh.logneko.presentation.composable.DesktopVerticalScroll
import app.brucehsieh.logneko.presentation.composable.FilterEditor
import app.brucehsieh.logneko.presentation.composable.LineNumber
import app.brucehsieh.logneko.presentation.composable.LineText
import app.brucehsieh.logneko.presentation.composable.NumberTextLazyList
import app.brucehsieh.logneko.presentation.composable.SearchHeader
import app.brucehsieh.logneko.presentation.theme.withFontFamily
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(FlowPreview::class)
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@Composable
@Preview
fun App(viewModel: MainScreenViewModel = koinViewModel()) {

    MaterialTheme(
        typography = MaterialTheme.typography.withFontFamily(FontFamily.Monospace)
    ) {
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        val lineItems = viewModel.lineItems.collectAsLazyPagingItems()
        val uiState = viewModel.uiState.collectAsState().value
        val currentPlatformFile = viewModel.currentPlatformFile.collectAsState().value

        LaunchedEffect(uiState.matchesByLine) {
            if (uiState.matchesByLine.isNotEmpty()) {
                println("@@@@: query matches: ${uiState.matchesByLine.size}")
            }
        }

        Row {
            AppNavigationRail(
                isFilterEnabled = currentPlatformFile != null,
                onOpenFile = viewModel::openFilePicker,
                onToggleFilterSheet = {
                    showBottomSheet = !showBottomSheet
                }
            )
            Box(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Search header (text search bar)
                    SearchHeader(
                        searchQuery = uiState.textQuery,
                        onQueryChange = viewModel::onTextQueryChange
                    )

                    if (uiState.filterQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            FlowRow {
                                InputChip(
                                    selected = true,
                                    onClick = viewModel::onFilterClear,
                                    label = { Text("Filter On") },
                                    trailingIcon = { Icon(Icons.Outlined.Close, contentDescription = "Close") }
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val listState = rememberLazyListState()
                        SelectionContainer {
                            if (uiState.filteredLineItems.isNotEmpty()) {
                                LazyColumn(state = listState) {
                                    items(uiState.filteredLineItems, key = { it.number }) { lineItem ->

                                        val matchRanges by remember(lineItem.number, uiState.matchesByLine) {
                                            derivedStateOf {
                                                uiState.matchesByLine[lineItem.number].orEmpty()
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            LineNumber(lineItem = lineItem, modifier = Modifier.width(64.dp))
                                            LineText(lineItem = lineItem, matchRanges = matchRanges)
                                        }
                                    }
                                }
                            } else {
                                NumberTextLazyList(
                                    lineItems = lineItems,
                                    listState = listState,
                                    matchesByLine = uiState.matchesByLine
                                )
                            }
                        }
                        DesktopVerticalScroll(
                            lazyListState = listState,
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                        )
                    }
                }

                if (currentPlatformFile != null && showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {
                        FilterEditor(
                            applyFilter = viewModel::onFilterApply,
                            onDismiss = { showBottomSheet = false },
                        )
                    }
                }
            }
        }
    }
}