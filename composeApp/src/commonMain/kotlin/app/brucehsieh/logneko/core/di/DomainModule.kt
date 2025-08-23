package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.domain.manager.DefaultTextSearchManager
import app.brucehsieh.logneko.domain.manager.TextSearchManager
import app.brucehsieh.logneko.domain.searching.InMemorySearcher
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    single { DefaultTextSearchManager(get()) }.bind(TextSearchManager::class)
    single { InMemorySearcher() }
}