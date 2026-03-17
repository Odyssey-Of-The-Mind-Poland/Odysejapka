package odyseja.odysejapka.exceptions

class CityNotFoundException(
    cityId: Int? = null,
    cityName: String? = null
): RuntimeException() {

    override val message: String =
        if (cityId != null) "Nie znaleziono miasta o ID $cityId"
        else "Nie znaleziono miasta o nazwie $cityName"
}