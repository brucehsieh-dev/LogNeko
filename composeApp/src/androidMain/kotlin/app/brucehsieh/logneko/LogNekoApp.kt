package app.brucehsieh.logneko

import android.app.Application
import app.brucehsieh.logneko.core.initializer.AppInitializer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LogNekoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppInitializer(this)
    }
}