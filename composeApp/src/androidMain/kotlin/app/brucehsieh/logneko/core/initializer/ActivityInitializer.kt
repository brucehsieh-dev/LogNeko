package app.brucehsieh.logneko.core.initializer

import androidx.activity.ComponentActivity
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class ActivityInitializer(activity: ComponentActivity) {
    init {
        FileKit.init(activity)
    }
}