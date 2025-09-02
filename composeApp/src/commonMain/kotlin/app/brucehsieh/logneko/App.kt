package app.brucehsieh.logneko

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import app.brucehsieh.logneko.presentation.composable.FilterChipRow
import app.brucehsieh.logneko.presentation.composable.FilterEditor
import app.brucehsieh.logneko.presentation.composable.LogLinePane
import app.brucehsieh.logneko.presentation.composable.SearchHeader
import app.brucehsieh.logneko.presentation.composable.ZoomableSurface
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

    MaterialTheme(typography = MaterialTheme.typography.withFontFamily(FontFamily.Monospace)) {
        // Hoisted states
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()
        val listState = rememberLazyListState()

        // Stable, memoized states
        val lineItems = viewModel.lineItems.collectAsLazyPagingItems()
        val uiState = viewModel.uiState.collectAsState().value

        // Memoize matches map to reduce downstream recompositions
        val matchesByLine = remember(uiState) { uiState.matchesByLine }

        LaunchedEffect(uiState.matchesByLine) {
            if (uiState.matchesByLine.isNotEmpty()) {
                println("@@@@: query matches: ${uiState.matchesByLine.size}")
            }
        }

        Row {
            AppNavigationRail(
                isFilterEnabled = uiState.hasFile,
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
                ZoomableSurface(
                    enableWheelZoom = true,
                    onZoomIn = viewModel::onZoomIn,
                    onZoomOut = viewModel::onZoomOut,
                    onZoomReset = viewModel::onZoomReset,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text("${uiState.activeSearchHitIndex.plus(1)} / ${uiState.searchHits.size}")

                        // Search header (text search bar)
                        SearchHeader(
                            searchQuery = uiState.textQuery,
                            onQueryChange = viewModel::onTextQueryChange,
                            onPrevious = viewModel::prevMatch,
                            onNext = viewModel::nextMatch
                        )

                        // Filter chip row (visible only when filter is active)
                        FilterChipRow(
                            filterQuery = uiState.filterQuery,
                            onClear = viewModel::onFilterClear
                        )

                        // Main log line pane (filtered list or paged list)
                        LogLinePane(
                            lineSource = uiState.lineSource,
                            displayedLineItems = uiState.displayedLineItems,
                            pagingItems = lineItems,
                            listState = listState,
                            matchesByLine = matchesByLine,
                            searchHits = uiState.searchHits,
                            activeSearchHitIndex = uiState.activeSearchHitIndex,
                            fontSize = uiState.fontSizeSp
                        )
                    }
                }

                if (uiState.hasFile && showBottomSheet) {
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