package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.InfoEntity
import odyseja.odysejapka.port.InfoUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/info")
class InfoController(
        private val infoUseCase: InfoUseCase
) {

    @GetMapping("/{city}")
    @ResponseBody
    fun getInfo(@PathVariable city: String): Iterable<InfoEntity?>? {
        return infoUseCase.getInfo(city)
    }

    @GetMapping()
    fun getAllInfo(): MutableIterable<InfoEntity?> {
     return infoUseCase.getAllInfo()
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    fun addInfo(@RequestBody infoEntity: List<InfoEntity>) : List<InfoEntity> {
        return infoUseCase.addInfo(infoEntity)
    }
}