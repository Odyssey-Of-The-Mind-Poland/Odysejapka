package odyseja.odysejapka.info

/**
 * Visual identity of an info tile, rendered by the clients (mobile app, web). An info can carry
 * its own style; when it does not, it inherits the style of its category, and then a default.
 *
 * The server stores only identifiers: [color] is one of [COLORS] and [icon] is the
 * kebab-case name of an icon from the shared icon set. The clients map them to concrete
 * shades per theme (light/dark) and glyphs.
 */
object InfoCategoryStyle {
    /** Bean Validation patterns for the `icon` / `color` fields of [Info] and [InfoCategory]. */
    const val COLOR_PATTERN = "blue|orange|purple|green|red"
    const val ICON_PATTERN = "[a-z0-9]+(-[a-z0-9]+)*"
    const val MAX_ICON_LENGTH = 64

    const val COLOR_MESSAGE = "Nieznany kolor '\${validatedValue}'. Dozwolone: blue, orange, purple, green, red"
    const val ICON_MESSAGE = "Nieprawidłowa nazwa ikony '\${validatedValue}'. Oczekiwany format: małe litery, cyfry i myślniki"

    val COLORS = COLOR_PATTERN.split("|")
    const val DEFAULT_ICON = "info-circle"

    /** Colour for an item that has none assigned yet; stable per id so tiles do not flicker. */
    fun defaultColor(id: Int): String = COLORS[Math.floorMod(id, COLORS.size)]
}
