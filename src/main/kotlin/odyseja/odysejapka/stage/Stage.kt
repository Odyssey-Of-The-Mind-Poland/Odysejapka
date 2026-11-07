package odyseja.odysejapka.stage

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import odyseja.odysejapka.info.InfoCategoryStyle

/** A stage of a city; [icon] and [color] style its tile in the app (null = the app's default). */
class Stage(
    val id: Int,
    val number: Int,
    val city: Int,
    val name: String,
    @field:Size(max = InfoCategoryStyle.MAX_ICON_LENGTH, message = InfoCategoryStyle.ICON_MESSAGE)
    @field:Pattern(regexp = InfoCategoryStyle.ICON_PATTERN, message = InfoCategoryStyle.ICON_MESSAGE)
    val icon: String? = null,
    @field:Pattern(regexp = InfoCategoryStyle.COLOR_PATTERN, message = InfoCategoryStyle.COLOR_MESSAGE)
    val color: String? = null
)
