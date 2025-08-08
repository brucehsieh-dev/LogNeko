package app.brucehsieh.logneko.core.di

import app.brucehsieh.logneko.data.repository.FileLineRepositoryImpl
import app.brucehsieh.logneko.domain.repository.FileLineRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::FileLineRepositoryImpl).bind(FileLineRepository::class)
}