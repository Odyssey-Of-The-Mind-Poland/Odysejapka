package odyseja.odysejapka.info.v3

import odyseja.odysejapka.info.InfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Info endpoint for clients that render tiles themselves: every info (and category) comes with
 * an icon name and an accent colour key, so the app needs no hardcoded styling.
 */
@RestController
@RequestMapping("/api/v3/info")
class InfoV3Controller(private val infoService: InfoService) {

    @GetMapping
    fun getInfo(@RequestParam(required = false) cityId: Int?): InfoV3Response {
        val categories = infoService.getInfoCategories().map { it.toInfoCategory() }
        val byId = categories.associateBy { it.id }
        return InfoV3Response(
            infos = infoService.getInfo(cityId).filterNotNull().map { InfoV3.from(it, byId[it.category]) },
            categories = categories.map { InfoCategoryV3.from(it) }
        )
    }
}
