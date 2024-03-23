package odyseja.odysejapka.qr

enum class DeviceType(val location: String) {
    ANDROID("https://play.google.com/store/games?hl=pl&gl=US"),
    IOS("https://www.apple.com/pl/app-store/"),
    OTHER("/static/download.html")
}