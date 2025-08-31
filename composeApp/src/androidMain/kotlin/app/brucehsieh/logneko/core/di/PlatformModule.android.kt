package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.data.CONTENT_URL
import app.brucehsieh.logneko.data.paging.ContentUriLineReader
import app.brucehsieh.logneko.data.paging.LineReader
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule() = module {
    single(named(CONTENT_URL)) { ContentUriLineReader() }.bind(LineReader::class)
}