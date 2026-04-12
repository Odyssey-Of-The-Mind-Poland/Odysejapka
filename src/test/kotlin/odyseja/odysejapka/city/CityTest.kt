package odyseja.odysejapka.city

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class CityTest: OdysejaDsl() {
    lateinit var cityRespondingClient: RespondingControllerClient<CityController>

    @BeforeEach
    fun cityTestSetUp() {
        cityClient.clearCities()
        cityRespondingClient = controllerClientFactory.respondingClient(CityController::class.java)
    }

    @Test
    fun `should create city`() {
        val cityRequest = CreateCityRequest("Konkurs")
        val city = cityClient.saveCity(cityRequest)

        Assertions.assertThat(city).isNotNull
        Assertions.assertThat(city.name).isEqualTo("Konkurs")
        Assertions.assertThat(city.id).isNotNull
    }

    @Test
    fun `should look up city by its name`() {
        val city = createCity("Znajdź Mnie")
        val foundCity = cityClient.getCityByName("Znajdź Mnie")

        Assertions.assertThat(foundCity).isNotNull
        Assertions.assertThat(foundCity.id).isEqualTo(city.id)
        Assertions.assertThat(foundCity.name).isEqualTo(city.name)
        Assertions.assertThat(foundCity.name).isEqualTo("Znajdź Mnie")
    }

    @Test
    fun `should delete city`() {
        val city = createCity("Usuwisko Dolne")
        cityClient.deleteCity(city.id)

        val response = cityRespondingClient.executeConsumer { controller ->
            controller.getCityByName("Usuwisko Dolne")
        }

        Assertions.assertThat(response.statusCode).isEqualTo(404)
    }

    @Test
    fun `should delete city tied to performances`() {
        val city = createCity("Konkurs")
        createPerformance(city.id)
        cityClient.deleteCity(city.id)

        val response = cityRespondingClient.executeConsumer {
            controller -> controller.getCityByName("Konkurs")
        }

        Assertions.assertThat(response.statusCode).isEqualTo(404)
    }

    @Test
    fun `should clear all cities`() {
        createCity("Konkurs 1")
        createCity("Konkurs 2")
        cityClient.clearCities()

        Assertions.assertThat(cityClient.getCities()).isEmpty()
    }

    @Test
    fun `should properly handle exceptions`() {
        var response = cityRespondingClient.executeConsumer {
                controller -> controller.getCityByName("Nieistniejów")
        }
        var detail = parseProblemDetail(response)

        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono miasta o nazwie Nieistniejów")
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")

        response = cityRespondingClient.executeConsumer {
                controller -> controller.getCity(941415125)
        }
        detail = parseProblemDetail(response)

        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono miasta o ID 941415125")
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")

        response = cityRespondingClient.executeConsumer {
                controller -> controller.deleteCity(941415125)
        }
        detail = parseProblemDetail(response)

        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono miasta o ID 941415125")
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
    }
}