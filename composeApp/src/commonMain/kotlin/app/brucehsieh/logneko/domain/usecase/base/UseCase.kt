package app.brucehsieh.logneko.domain.usecase.base

/**
 * Synchronous (non-suspending) use case contract.
 * Prefer [SuspendUseCase] for I/O or long-running work.
 */
fun interface UseCase<in P, out R> {
    operator fun invoke(param: P): R
}