package app.brucehsieh.logneko

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform