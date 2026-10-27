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
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class InfoV3Test : OdysejaDsl() {

    lateinit var infoClient: InfoController
    lateinit var infoRespondingClient: RespondingControllerClient<InfoController>
    lateinit var infoV3Client: InfoV3Controller

    @BeforeEach
    fun infoV3TestSetUp() {
        infoClient = controllerClientFactory.create(InfoController::class.java)
        infoRespondingClient = controllerClientFactory.respondingClient(InfoController::class.java)
        infoV3Client = controllerClientFactory.create(InfoV3Controller::class.java)
        infoClient.getInfoCategories().forEach { infoClient.deleteInfoCategory(it.id) }
    }

    @Test
    fun `should return the style of the info itself`() {
        val city = createCity("Gdynia")
        infoClient.addInfo(info(city.id, "Dojazd", icon = "map-01", color = "purple"))

        val tile = infoV3Client.getInfo(city.id).infos.single()

        Assertions.assertThat(tile.icon).isEqualTo("map-01")
        Assertions.assertThat(tile.color).isEqualTo("purple")
    }

    @Test
    fun `should inherit style from the category when the info has none`() {
        val city = createCity("Sopot")
        val saved = infoClient.addInfo(info(city.id, "Noclegi"))
        infoClient.updateInfoCategory(InfoCategory(saved.category, "Noclegi", "home-02", "green"))

        val tile = infoV3Client.getInfo(city.id).infos.single()

        Assertions.assertThat(tile.icon).isEqualTo("home-02")
        Assertions.assertThat(tile.color).isEqualTo("green")
    }

    @Test
    fun `should prefer own style over the category style`() {
        val city = createCity("Hel")
        val saved = infoClient.addInfo(info(city.id, "Plaża", icon = "sun", color = "orange"))
        infoClient.updateInfoCategory(InfoCategory(saved.category, "Plaża", "home-02", "green"))

        val tile = infoV3Client.getInfo(city.id).infos.single()

        Assertions.assertThat(tile.icon).isEqualTo("sun")
        Assertions.assertThat(tile.color).isEqualTo("orange")
    }

    @Test
    fun `should fall back to defaults when nothing is styled`() {
        val city = createCity("Łeba")
        val saved = infoClient.addInfo(info(city.id, "Wydmy"))

        val response = infoV3Client.getInfo(city.id)
        val tile = response.infos.single()

        Assertions.assertThat(tile.icon).isEqualTo(InfoCategoryStyle.DEFAULT_ICON)
        Assertions.assertThat(tile.color).isEqualTo(InfoCategoryStyle.defaultColor(saved.id))
        Assertions.assertThat(InfoCategoryStyle.COLORS).contains(tile.color)
        Assertions.assertThat(response.categories.single().icon).isEqualTo(InfoCategoryStyle.DEFAULT_ICON)
    }

    @Test
    fun `should return categories with their style`() {
        val city = createCity("Władysławowo")
        val saved = infoClient.addInfo(info(city.id, "Port"))
        infoClient.updateInfoCategory(InfoCategory(saved.category, "Port", "building-01", "red"))

        Assertions.assertThat(infoV3Client.getInfo(city.id).categories)
            .containsExactly(InfoCategoryV3(saved.category, "Port", "building-01", "red"))
    }

    @Test
    fun `should only list infos of the requested city`() {
        val first = createCity("Rewa")
        val second = createCity("Kosakowo")
        infoClient.addInfo(info(first.id, "Zatoka"))
        infoClient.addInfo(info(second.id, "Lotnisko"))

        val response = infoV3Client.getInfo(first.id)

        Assertions.assertThat(response.infos.map { it.city }).containsOnly(first.id)
    }

    @Test
    fun `should reject an unknown color or malformed icon when saving an info`() {
        val city = createCity("Orłowo")

        val badColor = infoRespondingClient.executeConsumer { it.addInfo(info(city.id, "Klif", color = "magenta")) }
        val badIcon = infoRespondingClient.executeConsumer { it.addInfo(info(city.id, "Klif", icon = "<b>")) }

        Assertions.assertThat(parseProblemDetail(badColor).status).isEqualTo(400)
        Assertions.assertThat(parseProblemDetail(badIcon).status).isEqualTo(400)
        Assertions.assertThat(infoV3Client.getInfo(city.id).infos).isEmpty()
    }

    @Test
    fun `should keep v1 endpoints unchanged for old apps`() {
        val city = createCity("Babie Doły")
        val saved = infoClient.addInfo(info(city.id, "Plac zabaw"))

        val v1 = infoClient.getInfoAndCategories(city.id)

        Assertions.assertThat(v1.keys).containsExactlyInAnyOrder("infos", "categories")
        Assertions.assertThat((v1["categories"] as List<*>).filterIsInstance<Map<*, *>>().map { it["id"] })
            .contains(saved.category)
    }

    private fun info(cityId: Int, categoryName: String, icon: String? = null, color: String? = null) = Info(
        id = 0,
        infoName = "Jak dojechać",
        infoText = "Opis",
        city = cityId,
        category = 0,
        sortNumber = 0,
        categoryName = categoryName,
        icon = icon,
        color = color
    )
}
