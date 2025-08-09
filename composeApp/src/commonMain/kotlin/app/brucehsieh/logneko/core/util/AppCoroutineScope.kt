package app.brucehsieh.logneko.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

val defaultScope by lazy { CoroutineScope(Dispatchers.Default) }
val indexingScope by lazy { CoroutineScope(Dispatchers.IO) }