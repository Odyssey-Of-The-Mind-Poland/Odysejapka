package odyseja.odysejapka.info.v3

import odyseja.odysejapka.info.Info
import odyseja.odysejapka.info.InfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Info endpoint for clients that render categories themselves: every category comes with
 * an icon name and an accent colour key, so the app needs no hardcoded category list.
 */
@RestController
@RequestMapping("/api/v3/info")
class InfoV3Controller(private val infoService: InfoService) {

    @GetMapping
    fun getInfo(@RequestParam(required = false) cityId: Int?): InfoV3Response {
        return InfoV3Response(
            infos = infoService.getInfo(cityId).filterNotNull(),
            categories = infoService.getInfoCategories().map { InfoCategoryV3.from(it.toInfoCategory()) }
        )
    }
}
