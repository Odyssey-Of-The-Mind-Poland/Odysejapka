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
        val city = createCity("Finał Ogólnopolski", KonkursLevel.FINAL)
        val infoToAdd1 = createInfo(
            infoName = 	"Dzięki, Patroni!",
            infoText = "<span style=\"font-weight: normal; background-color: rgba(0, 0, 255, 0);\" id=\"docs-internal-guid-bfb15133-7fff-15bf-778a-0a1b5cbb1f40\"><p dir=\"ltr\" style=\"line-height: 1.656; text-align: center; margin-top: 0pt; margin-bottom: 0pt; padding: 0pt 0pt 6pt;\"><span style=\"font-size: 18pt; font-family: Ubuntu, sans-serif; color: rgb(0, 51, 153); font-weight: 700; font-style: normal; font-variant: small-caps; text-decoration: none; vertical-align: baseline; white-space: pre-wrap;\">PATRONI HONOROWI</span></p><p dir=\"ltr\" style=\"line-height: 1.656; text-align: center; margin-top: 0pt; margin-bottom: 0pt; padding: 0pt 0pt 6pt;\"><span style=\"font-size: 18pt; font-family: Ubuntu, sans-serif; color: rgb(0, 0, 0); font-weight: 700; font-style: normal; font-variant: small-caps; text-decoration: none; vertical-align: baseline; white-space: pre-wrap;\">MINISTER EDUKACJI I NAUKI</span></p><p dir=\"ltr\" style=\"line-height: 1.656; text-align: center; margin-top: 0pt; margin-bottom: 0pt; padding: 0pt 0pt 6pt;\"><span style=\"font-size: 14pt; font-family: Ubuntu, sans-serif; color: rgb(0, 0, 0); font-weight: 700; font-style: normal; font-variant: small-caps; text-decoration: none; vertical-align: baseline; white-space: pre-wrap;\">Przemysław Czarnek</span></p><p dir=\"ltr\" style=\"line-height: 1.656; text-align: center; margin-top: 0pt; margin-bottom: 0pt; padding: 0pt 0pt 6pt;\"><span style=\"font-size: 18pt; font-family: Ubuntu, sans-serif; color: rgb(0, 0, 0); font-weight: 700; font-style: normal; font-variant: small-caps; text-decoration: none; vertical-align: baseline; white-space: pre-wrap;\">PREZYDENT MIASTA GDYNI</span></p><p dir=\"ltr\" style=\"line-height: 1.656; text-align: center; margin-top: 0pt; margin-bottom: 0pt; padding: 0pt 0pt 6pt;\"><span style=\"font-size: 14pt; font-family: Ubuntu, sans-serif; color: rgb(0, 0, 0); font-weight: 700; font-style: normal; font-variant: small-caps; text-decoration: none; vertical-align: baseline; white-space: pre-wrap;\">Wojciech Szczurek</span></p><p dir=\"ltr\" style=\"line-height: 1.38; text-align: center; margin-top: 0pt; margin-bottom: 6pt;\"><span style=\"font-weight:normal;\" id=\"docs-internal-guid-a303f0ac-7fff-fe2d-a8ab-f513b876fea2\"></span></p><p dir=\"ltr\" style=\"line-height: 1.656; text-align: center; margin-top: 0pt; margin-bottom: 6pt;\"><span style=\"font-size: 18pt; font-family: Ubuntu, sans-serif; color: rgb(0, 51, 153); font-weight: 700; font-style: normal; font-variant: small-caps; text-decoration: none; vertical-align: baseline; white-space: pre-wrap;\">DZIĘKUJEMY ZA WSPARCIE!</span></p></span>",
            cityId = city.id,
            category = 0,
            sortNumber = 1,
            categoryName = "Podziękowania"
        )

        infoClient.addInfo(infoToAdd1)
        var info = infoClient.getInfo(city.id)

        Assertions.assertThat(info).hasSize(1)
        Assertions.assertThat(info.first())
            .usingRecursiveComparison()
            .ignoringFields("id", "category", "categoryName")
            .isEqualTo(infoToAdd1)

        val infoToAdd2 = createInfo("Ważna informacja", "Zatkała się ubikacja", city.id)
        infoClient.addInfo(infoToAdd2)
        info = infoClient.getInfo(city.id)

        Assertions.assertThat(info).hasSize(2)
        Assertions.assertThat(info.last())
            .usingRecursiveComparison()
            .ignoringFields("id", "category", "categoryName")
            .isEqualTo(infoToAdd2)
    }

    @Test
    fun `should default to finals when cityId is null`() {
        clearCities()

        val final = createCity("Gdynia", KonkursLevel.FINAL)
        val regio = createCity("Kraków", KonkursLevel.REGIONAL)

        val finalInfo = createInfo("Informacje finałowe", cityId = final.id)
        val regioInfo = createInfo("Informacje regionalne", cityId = regio.id)

        infoClient.addInfo(finalInfo)
        infoClient.addInfo(regioInfo)
        val info = infoClient.getInfo(null)

        Assertions.assertThat(info).hasSize(1)
        Assertions.assertThat(info.first()!!.infoName).isEqualTo(finalInfo.infoName)
        Assertions.assertThat(info.first()!!.city).isEqualTo(final.id)
    }

    @Test
    fun `should return empty list when there is no info`() {
        val city = createCity("Miasto tajemnic", KonkursLevel.REGIONAL)
        Assertions.assertThatNoException().isThrownBy { infoClient.getInfo(city.id) }
        Assertions.assertThat(infoClient.getInfo(city.id)).isEmpty()
    }

    @Test
    fun `should throw exception city does not exist`() {
        clearCities()
        var response = infoRespondingClient.executeConsumer { controller -> controller.getInfo(null) }
        var detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono konkursu ogólnopolskiego")

        response = infoRespondingClient.executeConsumer { controller -> controller.getInfo(0) }
        detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono miasta o ID 0")
    }

    @Test
    fun `should update info`() {
        val city = createCity("Miasto poprawek")
        val oldInfo = infoClient.addInfo(createInfo(
            "Proszem o uwagem",
            "Gala została odwołana, bo Omera boli głowa. Można się rozejść.",
            city.id
        ))

        val newInfo = oldInfo.copy(
            infoName = "Proszę o uwagę",
            infoText = "Po szybkiej drzemce, Omer stwierdził, że Gala jednak się odbędzie."
        )
        infoClient.updateInfo(newInfo)

        val info = infoClient.getInfo(city.id)
        Assertions.assertThat(info).hasSize(1)
        Assertions.assertThat(info.first()!!.infoName).isEqualTo(newInfo.infoName)
        Assertions.assertThat(info.first()!!.infoText).isEqualTo(newInfo.infoText)
    }

    @Test
    fun `should delete info`() {
        val info = infoClient.addInfo(createInfo())
        Assertions.assertThat(infoClient.getInfo(info.city)).hasSize(1)

        infoClient.deleteInfo(info.id)
        Assertions.assertThat(infoClient.getInfo(info.city)).isEmpty()
    }

    @Test
    fun `should sort info by sortNumber`() {
        val city = createCity("Miasto lepszego sortu")
        infoClient.addInfo(createInfo(sortNumber = 1, infoName = "Czwarty", cityId = city.id))
        infoClient.addInfo(createInfo(sortNumber = 2, infoName = "Trzeci", cityId = city.id))
        infoClient.addInfo(createInfo(sortNumber = 4, infoName = "Pierwszy", cityId = city.id))
        infoClient.addInfo(createInfo(sortNumber = 3, infoName = "Drugi", cityId = city.id))

        val info = infoClient.getInfo(city.id)
        Assertions.assertThat(info).hasSize(4)
        Assertions.assertThat(info.elementAtOrNull(0)!!.infoName).isEqualTo("Pierwszy")
        Assertions.assertThat(info.elementAtOrNull(1)!!.infoName).isEqualTo("Drugi")
        Assertions.assertThat(info.elementAtOrNull(2)!!.infoName).isEqualTo("Trzeci")
        Assertions.assertThat(info.elementAtOrNull(3)!!.infoName).isEqualTo("Czwarty")
    }

    @Test
    fun `should properly handle exceptions`() {
        clearCities()
        val infoId = 0

        var response = infoRespondingClient.executeConsumer { controller -> controller.getInfoById(infoId) }
        var detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono informacji o ID $infoId")

        response = infoRespondingClient.executeConsumer { controller -> controller.updateInfo(createInfo(id = infoId)) }
        detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono informacji o ID $infoId")
    }

    private fun createInfo(
        infoName: String = "Ważne",
        infoText: String = "Lorem ipsum dolor sit amet",
        cityId: Int = createCity("Miasto").id,
        category: Int = 0,
        sortNumber: Int = 0,
        categoryName: String = "Dla deweloperów",
        id: Int = 0
    ): Info {
        return Info(
            id = id,
            infoName = infoName,
            infoText = infoText,
            city = cityId,
            category = category,
            sortNumber = sortNumber,
            categoryName = categoryName
        )
    }
}