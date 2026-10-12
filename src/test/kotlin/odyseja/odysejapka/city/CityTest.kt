package odyseja.odysejapka.city

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.test.context.support.WithMockUser
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class CityTest : OdysejaDsl() {

    @BeforeEach
    fun cityTestSetUp() {
        clearCities()
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
        val cityId = city.id
        cityClient.deleteCity(cityId)

        val response = getCityWithRespondingClient(cityId)
        Assertions.assertThat(response.status).isEqualTo(404)
    }

    @Test
    fun `should delete city tied to performances`() {
        val city = createCity("Konkurs z przedstawieniem")
        val cityId = city.id
        createPerformance(cityId)
        cityClient.deleteCity(cityId)

        val response = getCityWithRespondingClient(cityId)
        Assertions.assertThat(response.status).isEqualTo(404)
    }

    @Test
    fun `should update city`() {
        clearCities()
        val oldCity = createCity("Stare Miasto", KonkursLevel.REGIONAL)
        val newCity = CityEntity(oldCity.id, "Nowe Miasto", KonkursLevel.FINAL)
        cityClient.updateCity(newCity)

        Assertions.assertThat(cityClient.getCity(oldCity.id).name).isEqualTo("Nowe Miasto")
        Assertions.assertThat(cityClient.getCity(oldCity.id).level).isEqualTo(KonkursLevel.FINAL)
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

        Assertions.assertThatNoException().isThrownBy {
            timeTableClient.getPerformance(performance)
        }
        val newPerformance = timeTableClient.getPerformance(performance)
        Assertions.assertThat(newPerformance.cityName).isEqualTo("Nowe Miasto")
    }

    @Test
    fun `should properly handle exceptions`() {
        val response = getCityWithRespondingClient(941415125)

        Assertions.assertThat(response.status).isEqualTo(404)
        Assertions.assertThat(response.detail).isEqualTo("Nie znaleziono miasta o ID 941415125")
        Assertions.assertThat(response.title).isEqualTo("ENTITY NOT FOUND")
    }
}