package odyseja.odysejapka.city

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.test.context.support.WithMockUser
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class CityTest: OdysejaDsl() {

    @BeforeEach
    fun cityTestSetUp() {
        cityClient.clearCities()
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
        val cityRespondingClient = controllerClientFactory.respondingClient(CityController::class.java)
        val city = createCity("Usuwisko Dolne")
        cityClient.deleteCity(city.id)

        val response = cityRespondingClient.executeConsumer { controller ->
            controller.getCityByName("Usuwisko Dolne")
        }

        Assertions.assertThat(response.statusCode).isEqualTo(404)
        Assertions.assertThat(response.mockHttpServletResponse.contentAsString)
            .isEqualTo("Nie znaleziono miasta o nazwie Usuwisko Dolne")
    }

    @Test
    fun `should delete city tied to performances`() {
        val cityRespondingClient = controllerClientFactory.respondingClient(CityController::class.java)
        val city = createCity("Konkurs")
        createPerformance(city.id)
        cityClient.deleteCity(city.id)

        val response = cityRespondingClient.executeConsumer {
            controller -> controller.getCityByName("Konkurs")
        }

        Assertions.assertThat(response.statusCode).isEqualTo(404)
        Assertions.assertThat(response.mockHttpServletResponse.contentAsString)
            .isEqualTo("Nie znaleziono miasta o nazwie Konkurs")
    }

    @Test
    fun `should clear all cities`() {
        createCity("Konkurs 1")
        createCity("Konkurs 2")
        cityClient.clearCities()

        Assertions.assertThat(cityClient.getCities()).isEmpty()
    }

    @Test
    fun `should properly handle CityNotFoundException`() {
        val cityRespondingClient = controllerClientFactory.respondingClient(CityController::class.java)
        var response = cityRespondingClient.executeConsumer {
                controller -> controller.getCityByName("Nieistniejów")
        }
        Assertions.assertThat(response.statusCode).isEqualTo(404)
        Assertions.assertThat(response.mockHttpServletResponse.contentAsString)
            .isEqualTo("Nie znaleziono miasta o nazwie Nieistniejów")

        response = cityRespondingClient.executeConsumer {
                controller -> controller.getCity(941415125)
        }
        Assertions.assertThat(response.statusCode).isEqualTo(404)
        Assertions.assertThat(response.mockHttpServletResponse.contentAsString)
            .isEqualTo("Nie znaleziono miasta o ID 941415125")

        response = cityRespondingClient.executeConsumer {
                controller -> controller.deleteCity(941415125)
        }
        Assertions.assertThat(response.statusCode).isEqualTo(404)
        Assertions.assertThat(response.mockHttpServletResponse.contentAsString)
            .isEqualTo("Nie znaleziono miasta o ID 941415125")
    }
}