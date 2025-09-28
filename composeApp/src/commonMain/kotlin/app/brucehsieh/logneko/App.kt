package app.brucehsieh.logneko

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import app.brucehsieh.logneko.presentation.MainScreenViewModel
import app.brucehsieh.logneko.presentation.composable.AppBottomAppBar
import app.brucehsieh.logneko.presentation.composable.AppNavigationRail
import app.brucehsieh.logneko.presentation.composable.EdgeFontSizeAdjuster
import app.brucehsieh.logneko.presentation.composable.FilterChipRow
import app.brucehsieh.logneko.presentation.composable.LogLinePane
import app.brucehsieh.logneko.presentation.composable.TextSearchBar
import app.brucehsieh.logneko.presentation.composable.ZoomableSurface
import app.brucehsieh.logneko.presentation.composable.filter.FilterBuilder
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
        var showSearchUi by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()
        val listState = rememberLazyListState()

        // Stable, memoized states
        val lineItems = viewModel.lineItems.collectAsLazyPagingItems()
        val uiState = viewModel.uiState.collectAsState().value

        // Memoize matches map to reduce downstream recompositions
        val matchesByLine = remember(uiState) { uiState.matchesByLine }

        val windowAdaptiveInfo = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true)
        val windowSizeClass = windowAdaptiveInfo.windowSizeClass

        LaunchedEffect(showSearchUi) {
            if (!showSearchUi) viewModel.onTextQueryChange("")
        }

        Scaffold(
            bottomBar = {
                if (!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                    Column {

                        AnimatedVisibility(
                            visible = showSearchUi,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            TextSearchBar(
                                searchQuery = uiState.textQuery,
                                onSearchQueryChange = viewModel::onTextQueryChange,
                                onSearch = { /* handled in ViewModel */ },
                                activeMatch = uiState.activeSearchHitIndex.plus(1),
                                totalMatches = uiState.searchHits.size,
                                onPrevious = viewModel::prevMatch,
                                onNext = viewModel::nextMatch,
                                onDismiss = { showSearchUi = false },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        AppBottomAppBar(
                            hasFileLoaded = uiState.hasFile,
                            showSearchUi = showSearchUi,
                            onOpenFileUi = viewModel::openFilePicker,
                            onSearchUi = { showSearchUi = !showSearchUi },
                            onJumpToLineUi = {},
                            onFilterUi = { showBottomSheet = !showBottomSheet }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {

                if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                    AppNavigationRail(
                        isFilterEnabled = uiState.hasFile,
                        onOpenFile = viewModel::openFilePicker,
                        onToggleFilterSheet = { showBottomSheet = !showBottomSheet }
                    )
                }

                Box(
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    ZoomableSurface(
                        enableWheelZoom = true,
                        onZoomIn = viewModel::onZoomIn,
                        onZoomOut = viewModel::onZoomOut,
                        onZoomReset = viewModel::onZoomReset,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                                TextSearchBar(
                                    searchQuery = uiState.textQuery,
                                    onSearchQueryChange = viewModel::onTextQueryChange,
                                    onSearch = { /* handled in ViewModel */ },
                                    activeMatch = uiState.activeSearchHitIndex.plus(1),
                                    totalMatches = uiState.searchHits.size,
                                    onPrevious = viewModel::prevMatch,
                                    onNext = viewModel::nextMatch,
                                    onDismiss = {
                                        // TODO: Hide search bar
                                    }
                                )
                            }

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
                                fontSize = uiState.fontSizeSp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    EdgeFontSizeAdjuster(
                        value = uiState.fontSizeSp.value,
                        onChange = { viewModel.onFontSizeChange(it.sp) },
                        onChangeFinished = { /* persist via DataStore or use case, if you want */ }
                    )

                    if (uiState.hasFile && showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            FilterBuilder(
                                initialFilterExpression = uiState.filterExpression,
                                onFilterApply = viewModel::onFilterApply,
                                onDismiss = { showBottomSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}