package app.brucehsieh.logneko.domain.usecase.base

import app.brucehsieh.logneko.core.util.DefaultCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Base class for suspending use cases to centralize dispatcher and error handling.
 */
abstract class BaseSuspendUseCase<in P, out R>(
    private val dispatcher: CoroutineDispatcher = DefaultCoroutineDispatchers.default
) : SuspendUseCase<P, R> {

    /**
     * Template method that runs [execute] on [dispatcher].
     * Override [execute] to implement business logic only.
     */
    override suspend fun invoke(param: P): R = withContext(dispatcher) {
        execute(param)
    }

    protected abstract suspend fun execute(param: P): R
}