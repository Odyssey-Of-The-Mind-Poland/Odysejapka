package odyseja.odysejapka.info.v3

import odyseja.odysejapka.info.Info
import odyseja.odysejapka.info.InfoCategory
import odyseja.odysejapka.info.InfoCategoryStyle

data class InfoV3Response(
    val infos: List<Info>,
    val categories: List<InfoCategoryV3>
)

/**
 * Unlike the admin-facing [InfoCategory], icon and color are never null here: a category the
 * editors have not styled yet gets a stable default, so clients can render it without fallbacks.
 */
data class InfoCategoryV3(
    val id: Int,
    val name: String,
    val icon: String,
    val color: String
) {
    companion object {
        fun from(category: InfoCategory) = InfoCategoryV3(
            id = category.id,
            name = category.name,
            icon = category.icon ?: InfoCategoryStyle.DEFAULT_ICON,
            color = category.color ?: InfoCategoryStyle.defaultColor(category.id)
        )
    }
}
