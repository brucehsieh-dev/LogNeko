package app.brucehsieh.logneko.core.logging

interface AppLogger {
    fun d(tag: String, t: Throwable? = null, msg: () -> String)
    fun i(tag: String, t: Throwable? = null, msg: () -> String)
    fun w(tag: String, t: Throwable? = null, msg: () -> String)
    fun e(tag: String, t: Throwable? = null, msg: () -> String)
}