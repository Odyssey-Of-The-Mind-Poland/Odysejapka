package odyseja.odysejapka.info

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class Info(
    val id: Int,
    val infoName: String,
    val infoText: String,
    val city: Int,
    val category: Int,
    val sortNumber: Int,
    val categoryName: String,
    @field:Size(max = InfoCategoryStyle.MAX_ICON_LENGTH, message = InfoCategoryStyle.ICON_MESSAGE)
    @field:Pattern(regexp = InfoCategoryStyle.ICON_PATTERN, message = InfoCategoryStyle.ICON_MESSAGE)
    val icon: String? = null,
    @field:Pattern(regexp = InfoCategoryStyle.COLOR_PATTERN, message = InfoCategoryStyle.COLOR_MESSAGE)
    val color: String? = null
)
