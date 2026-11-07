package odyseja.odysejapka.timetable

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import odyseja.odysejapka.info.InfoCategoryStyle

/** An age group (0 = juniors, 1-4 = divisions I-IV) with the style of its tile in the app. */
class Age(
    @field:Min(0, message = "Dozwolone ID grupy wiekowej to: 0, 1, 2, 3 lub 4")
    @field:Max(4, message = "Dozwolone ID grupy wiekowej to: 0, 1, 2, 3 lub 4")
    val id: Int,
    @field:Size(max = InfoCategoryStyle.MAX_ICON_LENGTH, message = InfoCategoryStyle.ICON_MESSAGE)
    @field:Pattern(regexp = InfoCategoryStyle.ICON_PATTERN, message = InfoCategoryStyle.ICON_MESSAGE)
    val icon: String? = null,
    @field:Pattern(regexp = InfoCategoryStyle.COLOR_PATTERN, message = InfoCategoryStyle.COLOR_MESSAGE)
    val color: String? = null
)
