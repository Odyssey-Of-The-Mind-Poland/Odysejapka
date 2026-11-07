package odyseja.odysejapka.timetable

import jakarta.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/age", "/api/age"])
class AgeController(private val ageService: AgeService) {

    /** All five age groups; the ones nobody has styled yet come back without icon and colour. */
    @GetMapping
    fun getAges(): List<Age> = ageService.getAges()

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping
    fun updateAges(@Valid @RequestBody ages: List<Age>) = ageService.updateAges(ages)
}
