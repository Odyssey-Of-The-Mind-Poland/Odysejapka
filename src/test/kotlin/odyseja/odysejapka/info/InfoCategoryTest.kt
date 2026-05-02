package odyseja.odysejapka.info

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class InfoCategoryTest: OdysejaDsl() {

    lateinit var infoClient: InfoController
    lateinit var infoRespondingClient: RespondingControllerClient<InfoController>

    @BeforeEach
    fun infoCategoryTestSetUp() {
        infoClient = controllerClientFactory.create(InfoController::class.java)
        infoRespondingClient = controllerClientFactory.respondingClient(InfoController::class.java)
    }

    @Test
    fun `should create info category`() {
        clearInfoCategories()
        val category1 = InfoCategory(getNewInfoCategoryId(), "Dla deweloperów")
        val info1 = createInfo(category1)

        infoClient.addInfo(info1)
        var categories = infoClient.getInfoCategories()
        Assertions.assertThat(categories).hasSize(1)
        Assertions.assertThat(categories.first().name).isEqualTo(category1.name)

        val category2 = InfoCategory(getNewInfoCategoryId(), "Dla nie wiem kogo")
        val info2 = createInfo(category2)
        infoClient.addInfo(info2)
        categories = infoClient.getInfoCategories()
        Assertions.assertThat(categories).hasSize(2)
        Assertions.assertThat(categories.any { it.name == category2.name })
    }

    @Test
    fun `should delete categories`() {
        val category = InfoCategory(getNewInfoCategoryId(), "Dla szopów")
        val info = infoClient.addInfo(createInfo(category))
        val categoriesBefore = infoClient.getInfoCategories()

        infoClient.deleteInfoCategory(info.category)
        val categoriesAfter = infoClient.getInfoCategories()

        Assertions.assertThat(categoriesBefore).hasSize(categoriesAfter.count()+1)
        Assertions.assertThat(categoriesAfter.none { it.name == category.name })
    }

    @Test
    fun `should return empty list when there is no info category`() {
        clearInfoCategories()
        Assertions.assertThatNoException().isThrownBy { infoClient.getInfoCategories() }
        Assertions.assertThat(infoClient.getInfoCategories()).isEmpty()
    }

    @Test
    fun `should ignore category name if category already exists`() {
        clearInfoCategories()
        val category1 = InfoCategory(getNewInfoCategoryId(), "Dla wszystkich")
        val info1 = infoClient.addInfo(createInfo(category1))

        val category2 = InfoCategory(info1.category, "Dla wybranych")
        val info2 = infoClient.addInfo(createInfo(category2))

        println(infoClient.getInfoCategories())
        Assertions.assertThat(infoClient.getInfoCategories()).hasSize(1)
        Assertions.assertThat(infoClient.getInfoCategories().first().name).isEqualTo(category1.name)
        Assertions.assertThat(infoClient.getInfo(info1.city).first()!!.categoryName).isEqualTo("Dla wszystkich")
        Assertions.assertThat(infoClient.getInfo(info2.city).first()!!.categoryName).isEqualTo("Dla wszystkich")
    }

    private fun createInfo(category: InfoCategory): Info {
        return Info(
            0,
            "Kategorie info",
            "Kategorii nie da się zapisywać bezpośrednio. Dodajemy je do bazy danych razem z obiektem Info.",
            createCity("Konkurs").id,
            category.id,
            0,
            category.name
        )
    }

    private fun clearInfoCategories() {
        val categories = infoClient.getInfoCategories()
        categories.forEach {
            infoClient.deleteInfoCategory(it.id)
        }
    }

    private fun getNewInfoCategoryId(): Int {
        val categories = infoClient.getInfoCategories().sortedByDescending { it.id }
        return categories.firstOrNull()?.id?.plus(1) ?: 0
    }
}