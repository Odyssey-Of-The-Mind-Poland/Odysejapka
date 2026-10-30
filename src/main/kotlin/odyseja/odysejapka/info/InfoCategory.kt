package odyseja.odysejapka.info

import jakarta.validation.constraints.Pattern

data class InfoCategory(
  val id: Int,
  val name: String,
  @field:Pattern(regexp = InfoCategoryStyle.COLOR_PATTERN, message = InfoCategoryStyle.COLOR_MESSAGE)
  val color: String? = null
)
