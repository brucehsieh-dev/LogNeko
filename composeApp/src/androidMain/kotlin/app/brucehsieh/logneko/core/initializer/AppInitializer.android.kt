package app.brucehsieh.logneko.core.initializer

import android.app.Application
import app.brucehsieh.logneko.core.di.dataModule
import app.brucehsieh.logneko.core.di.platformModule
import app.brucehsieh.logneko.core.di.presentationModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

@ExperimentalCoroutinesApi
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppInitializer(application: Application) : Initializer {
    init {
        startKoin {
            androidContext(application)
            androidLogger()
            modules(dataModule, presentationModule, platformModule())
        }
    }
}