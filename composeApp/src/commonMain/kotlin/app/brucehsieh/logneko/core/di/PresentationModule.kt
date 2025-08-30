package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.presentation.MainScreenViewModel
import app.brucehsieh.logneko.presentation.search.DefaultSearchNavigator
import app.brucehsieh.logneko.presentation.search.SearchNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val presentationModule = module {
    viewModelOf(::MainScreenViewModel)
    factory { DefaultSearchNavigator() }.bind(SearchNavigator::class)
}