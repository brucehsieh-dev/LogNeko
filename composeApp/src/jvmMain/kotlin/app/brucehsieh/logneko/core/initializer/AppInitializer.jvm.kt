package app.brucehsieh.logneko.core.initializer

import app.brucehsieh.logneko.core.di.dataModule
import app.brucehsieh.logneko.core.di.platformModule
import app.brucehsieh.logneko.core.di.presentationModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.context.startKoin

@ExperimentalCoroutinesApi
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppInitializer : Initializer {
    init {
        startKoin {
            modules(dataModule, presentationModule, platformModule())
        }
    }
}