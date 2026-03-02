package odyseja.odysejapka.city

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.springframework.security.test.context.support.WithMockUser
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class CityTest: OdysejaDsl() {

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
        Assertions.assertThat(foundCity?.id).isEqualTo(city.id)
        Assertions.assertThat(foundCity?.name).isEqualTo(city.name)
        Assertions.assertThat(foundCity?.name).isEqualTo("Znajdź Mnie")
    }

    @Test
    fun `should delete city`() {
        val city = createCity("Usuwisko Dolne")
        cityClient.deleteCity(city.id)

        Assertions.assertThatThrownBy {
            cityClient.getCityByName("Usuwisko Dolne")
        }
            .hasRootCauseInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Nie ma miasta o nazwie Usuwisko Dolne.")
    }

    @Test
    fun `should delete city tied to performances`() {
        val city = createCity("Konkurs")
        createPerformance(city.id)
        cityClient.deleteCity(city.id)

        Assertions.assertThatThrownBy {
            cityClient.getCityByName("Konkurs")
        }
            .hasRootCauseInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Nie ma miasta o nazwie Konkurs.")
    }

    @Test
    fun `should clear all cities`() {
        createCity("Konkurs 1")
        createCity("Konkurs 2")
        cityClient.clearCities()

        Assertions.assertThat(cityClient.getCities()).isEmpty()
    }
}