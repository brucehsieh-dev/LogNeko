package app.brucehsieh.logneko.core.util

/**
 * Singleton utility for detecting the current operating system.
 */
object OperatingSystem {

    /**
     * Enum describing supported operating systems.
     */
    enum class OperatingSystemType {
        ANDROID,
        WINDOWS,
        MAC,
        LINUX,
        OTHER,
        UNKNOWN
    }

    /** The current operating system type. */
    fun type(): OperatingSystemType {
        if (isAndroid) return OperatingSystemType.ANDROID

        val os = osName.lowercase()
        return when {
            os.contains("win") -> OperatingSystemType.WINDOWS
            os.contains("mac") -> OperatingSystemType.MAC
            os.contains("nux") || os.contains("nix") -> OperatingSystemType.LINUX
            os.isNotBlank() -> OperatingSystemType.OTHER
            else -> OperatingSystemType.UNKNOWN
        }
    }

    /**
     * True if running on Android.
     */
    val isAndroid
        get() = try {
            Class.forName("android.os.Build")
            true
        } catch (_: ClassNotFoundException) {
            false
        }

    /**
     * True if running on any desktop OS (Windows, macOS, Linux, Other).
     */
    val isDesktop get() = !isAndroid

    /**
     * Raw JVM OS name, e.g. "Windows 11", "Mac OS X", "Linux".
     */
    val osName get() = System.getProperty("os.name") ?: "Unknown"

    /**
     * Convenience checks for desktop OSes.
     */
    fun isWindows() = !isAndroid && osName.contains("win", ignoreCase = true)
    fun isMac() = !isAndroid && osName.contains("mac", ignoreCase = true)
    fun isLinux() =
        !isAndroid && (osName.contains("nux", ignoreCase = true) || osName.contains("nix", ignoreCase = true))
}