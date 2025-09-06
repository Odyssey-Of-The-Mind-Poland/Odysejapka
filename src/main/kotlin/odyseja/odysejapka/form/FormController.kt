package odyseja.odysejapka.form

import odyseja.odysejapka.problem.ProblemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/form")
class FormController(private val formService: FormService) {

    @PutMapping("/{problem}")
    fun setFormEntries(@PathVariable problem: Int, @RequestBody formEntries: List<FormEntry>) {
        formService.setFormEntries(problem, formEntries)
    }

    @GetMapping("/{problem}")
    fun getFormEntries(@PathVariable problem: Int): List<FormEntry> {
        return formService.getFormEntries(problem)
    }
}