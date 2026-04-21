package odyseja.odysejapka.city

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class CityTest : OdysejaDsl() {
    lateinit var cityRespondingClient: RespondingControllerClient<CityController>

    @BeforeEach
    fun cityTestSetUp() {
        clearCities()
        cityRespondingClient = controllerClientFactory.respondingClient(CityController::class.java)
    }

    @Test
    fun `should create city`() {
        var cityRequest = CreateCityRequest("Konkurs", KonkursLevel.FINAL)
        var city = cityClient.saveCity(cityRequest)

        Assertions.assertThat(city).isNotNull
        Assertions.assertThat(city.name).isEqualTo("Konkurs")
        Assertions.assertThat(city.id).isNotNull
        Assertions.assertThat(city.level).isEqualTo(KonkursLevel.FINAL)

        cityRequest = CreateCityRequest("agawgal2572%@&%)q#^!?+", KonkursLevel.REGIONAL)
        city = cityClient.saveCity(cityRequest)
        Assertions.assertThat(city).isNotNull
        Assertions.assertThat(city.name).isEqualTo("agawgal2572%@&%)q#^!?+")
        Assertions.assertThat(city.id).isNotNull
        Assertions.assertThat(city.level).isEqualTo(KonkursLevel.REGIONAL)
    }

    @Test fun `should return all cities`() {
        clearCities()
        for (i in 1..10) {
            createCity("Miasto $i")
        }

        val cities = cityClient.getCities()
        Assertions.assertThat(cities).hasSize(10)
        Assertions.assertThat(cities.first()!!.name).isEqualTo("Miasto 1")
    }

    @Test
    fun `should delete city`() {
        val city = createCity("Usuwisko Dolne")
        cityClient.deleteCity(city.id)

        val response = getCityByName("Usuwisko Dolne")

        Assertions.assertThat(response).isNull()
    }

    @Test
    fun `should delete city tied to performances`() {
        val city = createCity("Konkurs z przedstawieniem")
        createPerformance(city.id)
        cityClient.deleteCity(city.id)

        val response = getCityByName("Konkurs z przedstawieniem")

        Assertions.assertThat(response).isNull()
    }

    @Test
    fun `should update city`() {
        clearCities()
        val oldCity = createCity("Stare Miasto", KonkursLevel.REGIONAL)
        val newCity = CityEntity(oldCity.id, "Nowe Miasto", KonkursLevel.FINAL)
        cityClient.updateCity(newCity)

        Assertions.assertThat(cityClient.getCity(oldCity.id).name).isEqualTo("Nowe Miasto")
        Assertions.assertThat(cityClient.getCity(oldCity.id).level).isEqualTo(KonkursLevel.FINAL)
        Assertions.assertThat(getCityByName("Stare Miasto")).isNull()
    }

    @Test
    fun `should update city tied to performances`() {
        clearCities()
        val oldCity = createCity("Stare Miasto", KonkursLevel.REGIONAL)
        val performance = createPerformance(oldCity.id)
        val newCity = CityEntity(oldCity.id, "Nowe Miasto", KonkursLevel.FINAL)
        Assertions.assertThatNoException().isThrownBy {
            cityClient.updateCity(newCity)
        }

        Assertions.assertThat(cityClient.getCity(oldCity.id).name).isEqualTo("Nowe Miasto")
        Assertions.assertThat(cityClient.getCity(oldCity.id).level).isEqualTo(KonkursLevel.FINAL)
        Assertions.assertThat(getCityByName("Stare Miasto")).isNull()

        Assertions.assertThatNoException().isThrownBy {
            timeTableClient.getPerformance(performance)
        }
        val newPerformance = timeTableClient.getPerformance(performance)
        Assertions.assertThat(newPerformance.city).isEqualTo("Nowe Miasto")
    }

    @Test
    fun `should properly handle exceptions`() {
        val response = cityRespondingClient.executeConsumer {
                controller -> controller.getCity(941415125)
        }
        val detail = parseProblemDetail(response)

        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono miasta o ID 941415125")
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
    }
}