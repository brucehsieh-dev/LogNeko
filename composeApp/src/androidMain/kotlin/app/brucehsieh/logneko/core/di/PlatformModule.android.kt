package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.data.paging.AndroidFileLinePagingSourceFactory
import app.brucehsieh.logneko.data.CONTENT_URL
import app.brucehsieh.logneko.data.paging.FileLinePagingSourceFactory
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule() = module {
    factory(named(CONTENT_URL)) { (uriString: String) -> AndroidFileLinePagingSourceFactory(uriString) }
        .bind(FileLinePagingSourceFactory::class)
}