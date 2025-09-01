package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.presentation.MainScreenViewModel
import app.brucehsieh.logneko.presentation.search.DefaultSearchNavigator
import app.brucehsieh.logneko.presentation.search.SearchNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(FlowPreview::class)
@ExperimentalCoroutinesApi
val presentationModule = module {
    viewModel { MainScreenViewModel(get()) }
    factory { DefaultSearchNavigator() }.bind(SearchNavigator::class)
}