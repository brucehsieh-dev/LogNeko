package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.data.JVM_FILE
import app.brucehsieh.logneko.data.paging.JvmFileLinePagingSourceFactory
import app.brucehsieh.logneko.core.paging.FileLinePagingSourceFactory
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule() = module {
    factory(named(JVM_FILE)) { (filePath: String) -> JvmFileLinePagingSourceFactory(filePath) }
        .bind(FileLinePagingSourceFactory::class)
}