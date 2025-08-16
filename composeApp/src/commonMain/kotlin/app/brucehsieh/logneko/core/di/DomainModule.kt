package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.domain.manager.DefaultTextSearchManager
import app.brucehsieh.logneko.domain.searching.InMemorySearcher
import app.brucehsieh.logneko.domain.searching.SearchEngine
import app.brucehsieh.logneko.domain.searching.TextSearchEngine
import app.brucehsieh.logneko.domain.manager.TextSearchManager
import org.apache.lucene.store.FSDirectory
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    single { (fsDirectory: FSDirectory) -> TextSearchEngine(fsDirectory) }.bind(SearchEngine::class)
    single { DefaultTextSearchManager(get()) }.bind(TextSearchManager::class)
    single { InMemorySearcher() }
}