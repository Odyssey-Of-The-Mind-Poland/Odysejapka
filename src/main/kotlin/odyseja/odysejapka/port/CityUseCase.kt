package odyseja.odysejapka.port

import odyseja.odysejapka.domain.CityEntity

interface CityUseCase {

  fun getCities(): MutableIterable<CityEntity?>
}