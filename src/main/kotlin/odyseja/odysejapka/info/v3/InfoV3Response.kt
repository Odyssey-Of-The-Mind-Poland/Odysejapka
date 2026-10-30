package odyseja.odysejapka.info.v3

import odyseja.odysejapka.info.Info
import odyseja.odysejapka.info.InfoCategory
import odyseja.odysejapka.info.InfoCategoryStyle

data class InfoV3Response(
    val infos: List<InfoV3>,
    val categories: List<InfoCategoryV3>
)

/**
 * An info tile with its style resolved: the info's own icon wins over the one global default icon (categories have no icon), and its
 * own color over the category's, then a default. Icon and color are never null, so clients render tiles without fallbacks.
 */
data class InfoV3(
    val id: Int,
    val infoName: String,
    val infoText: String,
    val city: Int,
    val category: Int,
    val sortNumber: Int,
    val categoryName: String,
    val icon: String,
    val color: String
) {
    companion object {
        fun from(info: Info, category: InfoCategory?) = InfoV3(
            id = info.id,
            infoName = info.infoName,
            infoText = info.infoText,
            city = info.city,
            category = info.category,
            sortNumber = info.sortNumber,
            categoryName = info.categoryName,
            icon = info.icon ?: InfoCategoryStyle.DEFAULT_ICON,
            color = info.color ?: category?.color ?: InfoCategoryStyle.defaultColor(info.id)
        )
    }
}

data class InfoCategoryV3(
    val id: Int,
    val name: String,
    val color: String
) {
    companion object {
        fun from(category: InfoCategory) = InfoCategoryV3(
            id = category.id,
            name = category.name,
            color = category.color ?: InfoCategoryStyle.defaultColor(category.id)
        )
    }
}
