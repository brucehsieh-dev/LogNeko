package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.presentation.MainScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val presentationModule = module {
    viewModelOf(::MainScreenViewModel)
}