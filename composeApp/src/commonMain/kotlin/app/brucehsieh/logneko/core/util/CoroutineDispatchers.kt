package app.brucehsieh.logneko.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Dispatchers provider interface.
 */
interface CoroutineDispatchers {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}

/**
 * Default dispatchers provider.
 */
object DefaultCoroutineDispatchers : CoroutineDispatchers {
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val main: CoroutineDispatcher = Dispatchers.Main
}
