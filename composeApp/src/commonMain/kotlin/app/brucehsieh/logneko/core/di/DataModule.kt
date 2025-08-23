package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.data.JVM_FILE
import app.brucehsieh.logneko.data.paging.JvmLineReader
import app.brucehsieh.logneko.data.paging.LineReader
import app.brucehsieh.logneko.data.repository.FileLineRepositoryImpl
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::FileLineRepositoryImpl).bind(FileLineRepository::class)
    single(named(JVM_FILE)) { (filePath: String) -> JvmLineReader(filePath) }.bind(LineReader::class)
}