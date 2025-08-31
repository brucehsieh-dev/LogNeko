package app.brucehsieh.logneko.domain.usecase.base

/**
 * Suspending use case contract with a single input param.
 * Use [Unit] if there is no input.
 */
fun interface SuspendUseCase<in P, out R> {
    suspend operator fun invoke(param: P): R
}