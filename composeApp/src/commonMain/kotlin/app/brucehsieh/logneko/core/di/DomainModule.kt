package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.domain.searching.TextSearchEngine
import org.apache.lucene.store.FSDirectory
import app.brucehsieh.logneko.domain.searching.SearchEngine
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    single { (fsDirectory: FSDirectory) -> TextSearchEngine(fsDirectory) }.bind(SearchEngine::class)
}