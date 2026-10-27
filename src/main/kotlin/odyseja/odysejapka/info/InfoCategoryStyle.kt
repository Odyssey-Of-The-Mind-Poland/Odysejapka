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
    val COLORS = listOf("blue", "orange", "purple", "green", "red")
    const val DEFAULT_ICON = "info-circle"

    private val ICON_FORMAT = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")
    private const val MAX_ICON_LENGTH = 64

    fun validate(icon: String?, color: String?) {
        require(color == null || color in COLORS) {
            "Nieznany kolor kategorii '$color'. Dozwolone: ${COLORS.joinToString()}"
        }
        require(icon == null || (icon.length <= MAX_ICON_LENGTH && ICON_FORMAT.matches(icon))) {
            "Nieprawidłowa nazwa ikony '$icon'. Oczekiwany format: małe litery, cyfry i myślniki"
        }
    }

    /** Colour for an item that has none assigned yet; stable per id so tiles do not flicker. */
    fun defaultColor(id: Int): String = COLORS[Math.floorMod(id, COLORS.size)]
}
