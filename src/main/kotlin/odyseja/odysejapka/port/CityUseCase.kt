package odyseja.odysejapka.port

import odyseja.odysejapka.domain.CityEntity

interface CityUseCase {

  fun getCities(): MutableIterable<CityEntity?>

  fun addCity(city: CityEntity)

  fun deleteCity(cityId: Int)
}