package odyseja.odysejapka.info

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.city.KonkursLevel
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class InfoTest: OdysejaDsl() {

    lateinit var infoClient: InfoController
    lateinit var infoRespondingClient: RespondingControllerClient<InfoController>

    @BeforeEach
    fun infoTestSetUp() {
        infoClient = controllerClientFactory.create(InfoController::class.java)
        infoRespondingClient = controllerClientFactory.respondingClient(InfoController::class.java)
    }

    @Test
    fun `should add and return info`() {
        clearCities()
        val city = createCity("Konkurs", KonkursLevel.FINAL)

        val infoToAdd1 = Info(
            id = 0,
            infoName = "Czołem!",
            infoText = "Tworzymy Info, zobaczymy jak pójdzie.",
            city = city.id,
            category = 0,
            sortNumber = 0,
            categoryName = "Dla deweloperów"
        )

        infoClient.addInfo(infoToAdd1)
        var info = infoClient.getInfo(city.id)

        Assertions.assertThat(info).hasSize(1)
        Assertions.assertThat(info!!.first())
            .usingRecursiveComparison()
            .ignoringFields("id", "category", "categoryName")
            .isEqualTo(infoToAdd1)

        val infoToAdd2 = infoToAdd1.copy(
            infoName = "Ważna informacja",
            infoText = "Zatkała się ubikacja"
        )
        infoClient.addInfo(infoToAdd2)
        info = infoClient.getInfo(city.id)

        Assertions.assertThat(info).hasSize(2)
        Assertions.assertThat(info!!.last())
            .usingRecursiveComparison()
            .ignoringFields("id", "category", "categoryName")
            .isEqualTo(infoToAdd2)
    }
}