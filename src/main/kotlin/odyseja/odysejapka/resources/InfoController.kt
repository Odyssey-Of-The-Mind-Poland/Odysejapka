package odyseja.odysejapka.resources

import odyseja.odysejapka.data.model.Info
import odyseja.odysejapka.repository.InfoRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/info")
class InfoController(
        private val infoRepository: InfoRepository
) {

    @GetMapping("/{city}")
    @ResponseBody
    fun getInfo(@PathVariable city: String): Iterable<Info?>? {
        return infoRepository.findByCity(city)
    }

    @GetMapping()
    fun getAllInfo(): MutableIterable<Info?> {
     return infoRepository.findAll()
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    fun addInfo(@RequestBody info: List<Info>) : List<Info>{
        infoRepository.saveAll(info)
        return info
    }
}