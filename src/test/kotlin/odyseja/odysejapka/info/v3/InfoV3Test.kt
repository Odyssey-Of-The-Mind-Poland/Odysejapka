package odyseja.odysejapka.info.v3

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.info.Info
import odyseja.odysejapka.info.InfoCategory
import odyseja.odysejapka.info.InfoCategoryStyle
import odyseja.odysejapka.info.InfoController
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class InfoV3Test : OdysejaDsl() {

    lateinit var infoClient: InfoController
    lateinit var infoV3Client: InfoV3Controller

    @BeforeEach
    fun infoV3TestSetUp() {
        infoClient = controllerClientFactory.create(InfoController::class.java)
        infoV3Client = controllerClientFactory.create(InfoV3Controller::class.java)
        infoClient.getInfoCategories().forEach { infoClient.deleteInfoCategory(it.id) }
    }

    @Test
    fun `should return categories with icon and color`() {
        val city = createCity("Gdynia")
        val info = infoClient.addInfo(info(city.id, "Dojazd"))
        infoClient.updateInfoCategory(InfoCategory(info.category, "Dojazd", "map-01", "purple"))

        val response = infoV3Client.getInfo(city.id)

        Assertions.assertThat(response.infos).extracting<String> { it.infoName }.containsExactly("Jak dojechać")
        Assertions.assertThat(response.categories)
            .containsExactly(InfoCategoryV3(info.category, "Dojazd", "map-01", "purple"))
    }

    @Test
    fun `should default icon and color of a category nobody styled`() {
        val city = createCity("Sopot")
        val info = infoClient.addInfo(info(city.id, "Noclegi"))

        val category = infoV3Client.getInfo(city.id).categories.single()

        Assertions.assertThat(category.icon).isEqualTo(InfoCategoryStyle.DEFAULT_ICON)
        Assertions.assertThat(category.color).isEqualTo(InfoCategoryStyle.defaultColor(info.category))
        Assertions.assertThat(InfoCategoryStyle.COLORS).contains(category.color)
    }

    @Test
    fun `should only list infos of the requested city`() {
        val first = createCity("Hel")
        val second = createCity("Władysławowo")
        infoClient.addInfo(info(first.id, "Plaża"))
        infoClient.addInfo(info(second.id, "Port"))

        val response = infoV3Client.getInfo(first.id)

        Assertions.assertThat(response.infos).hasSize(1)
        Assertions.assertThat(response.infos.single().city).isEqualTo(first.id)
    }

    @Test
    fun `should keep v1 category endpoint unchanged for old apps`() {
        val city = createCity("Łeba")
        val info = infoClient.addInfo(info(city.id, "Wydmy"))

        val v1 = infoClient.getInfoAndCategories(city.id)

        Assertions.assertThat(v1.keys).containsExactlyInAnyOrder("infos", "categories")
        Assertions.assertThat((v1["categories"] as List<*>).filterIsInstance<Map<*, *>>().map { it["id"] })
            .contains(info.category)
    }

    private fun info(cityId: Int, categoryName: String) = Info(
        id = 0,
        infoName = "Jak dojechać",
        infoText = "Opis",
        city = cityId,
        category = 0,
        sortNumber = 0,
        categoryName = categoryName
    )
}
