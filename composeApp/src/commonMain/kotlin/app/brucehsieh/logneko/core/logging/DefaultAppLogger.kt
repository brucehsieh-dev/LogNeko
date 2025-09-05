package app.brucehsieh.logneko.core.logging

import co.touchlab.kermit.Logger

object DefaultAppLogger : AppLogger {
    private val logger = Logger
    override fun d(tag: String, t: Throwable?, msg: () -> String) = logger.d(tag, t, msg)
    override fun i(tag: String, t: Throwable?, msg: () -> String) = logger.i(tag, t, msg)
    override fun w(tag: String, t: Throwable?, msg: () -> String) = logger.w(tag, t, msg)
    override fun e(tag: String, t: Throwable?, msg: () -> String) = logger.e(tag, t, msg)
}

fun Any.logD(message: String, t: Throwable? = null, tag: String? = null) =
    DefaultAppLogger.d(tag = tag ?: this.javaClass.simpleName, t = t, msg = { message })

fun Any.logD(t: Throwable? = null, tag: String? = null, message: () -> String) =
    DefaultAppLogger.d(tag = tag ?: this.javaClass.simpleName, t = t, msg = message)

fun Any.logI(message: String, t: Throwable? = null, tag: String? = null) =
    DefaultAppLogger.i(tag = tag ?: this.javaClass.simpleName, t = t, msg = { message })

fun Any.logI(t: Throwable? = null, tag: String? = null, message: () -> String) =
    DefaultAppLogger.i(tag = tag ?: this.javaClass.simpleName, t = t, msg = message)

fun Any.logW(message: String, t: Throwable? = null, tag: String? = null) =
    DefaultAppLogger.w(tag = tag ?: this.javaClass.simpleName, t = t, msg = { message })

fun Any.logW(t: Throwable? = null, tag: String? = null, message: () -> String) =
    DefaultAppLogger.w(tag = tag ?: this.javaClass.simpleName, t = t, msg = message)

fun Any.logE(message: String, t: Throwable? = null, tag: String? = null) =
    DefaultAppLogger.e(tag = tag ?: this.javaClass.simpleName, t = t, msg = { message })

fun Any.logE(t: Throwable? = null, tag: String? = null, message: () -> String) =
    DefaultAppLogger.e(tag = tag ?: this.javaClass.simpleName, t = t, msg = message)