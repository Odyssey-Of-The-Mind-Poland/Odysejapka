package odyseja.odysejapka.form

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/form")
class FormController(private val formService: FormService?) {

    @PutMapping("/{problem}")
    fun setProblemForm(@PathVariable problem: Int, @RequestBody problemForm: ProblemForm) {
        formService!!.setFormEntries(problem, problemForm)
    }

    @GetMapping("/{problem}")
    fun getProblemForm(@PathVariable problem: Int): ProblemForm {
        return formService!!.getFormEntries(problem)
    }

    @PostMapping("/{problem}/judge-count")
    fun setJudgesCount(@PathVariable problem: Int, @RequestParam cityId: Int, @RequestBody count: Int): Int {
        return formService!!.setJudgeCount(problem, cityId, count)
    }

    @GetMapping("/{problem}/judge-count")
    fun getJudgeCount(@PathVariable problem: Int, @RequestParam cityId: Int): Int {
        return formService!!.getJudgeCount(problem, cityId)
    }

    @PutMapping("/{performanceId}/result")
    fun setTeamResults(@PathVariable performanceId: Int, @RequestBody result: PerformanceResultsRequest) {
        formService!!.setTeamResults(performanceId, result)
    }

    @GetMapping("/{performanceId}/result")
    fun getTeamResults(@PathVariable performanceId: Int): TeamForm {
        return formService!!.getTeamForm(performanceId)
    }

    @GetMapping("/subjective-ranges")
    fun getSubjectiveRanges(): List<SubjectiveRangeDto> {
        return SubjectiveRanges.entries.map { it.toRangesResponse() }
    }
}