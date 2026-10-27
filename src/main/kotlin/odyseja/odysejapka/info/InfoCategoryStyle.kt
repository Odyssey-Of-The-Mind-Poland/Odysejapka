package odyseja.odysejapka.info

/**
 * Visual identity of an info category, rendered by the clients (mobile app, web).
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

    /** Colour for a category that has none assigned yet; stable per category so tiles do not flicker. */
    fun defaultColor(categoryId: Int): String = COLORS[Math.floorMod(categoryId, COLORS.size)]
}
