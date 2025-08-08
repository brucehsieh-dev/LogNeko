package app.brucehsieh.logneko.core.util

/**
 * Supported platforms.
 */
enum class Platform {
    ANDROID,
    DESKTOP
}

expect fun getPlatform(): Platform