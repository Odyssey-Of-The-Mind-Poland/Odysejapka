package odyseja.odysejapka.dashboard

import odyseja.odysejapka.city.CityEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dashboard/cities")
class CityFormController(
    private val cityAccessService: CityAccessService
) {

    @GetMapping
    fun getCities(@AuthenticationPrincipal principal: Any?): List<CityEntity> {
        val userId = extractUserId(principal) ?: return emptyList()
        return cityAccessService.getAccessibleCities(userId)
    }
}
