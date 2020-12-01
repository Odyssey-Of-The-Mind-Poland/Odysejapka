package Odyseja.Odysejapka.Resources.Admin

import Odyseja.Odysejapka.data.model.Age
import Odyseja.Odysejapka.data.model.Performance
import Odyseja.Odysejapka.data.model.Problem
import Odyseja.Odysejapka.data.model.Stage
import Odyseja.Odysejapka.repository.AgeRepository
import Odyseja.Odysejapka.repository.PerformanceRepository
import Odyseja.Odysejapka.repository.ProblemRepository
import Odyseja.Odysejapka.repository.StageRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@Secured("ROLE_ADMIN")
@RequestMapping("/admin/timeTable")
class PerformanceController(
        private val timeTableRepository: PerformanceRepository,
        private val problemRepository: ProblemRepository,
        private val stageRepository: StageRepository,
        private val ageRepository: AgeRepository
) {
    @GetMapping()
    fun getAll(): List<PerformanceDTO> {
        return timeTableRepository.findAll().map { PerformanceDTO(
                it!!.id,
                it.city,
                it.team,
                it.problem.id,
                it.age.id,
                it.stage.id,
                it.performance,
                it.spontan
        ) }
    }

    @PostMapping()
    @ResponseBody
    fun addPerformance(@RequestBody performances: List<PerformanceDTO>): List<Performance> {
        timeTableRepository.deleteByCity(performances.get(0).city)
        val per: List<Performance> = performances.map {
            Performance(
                    it.id,
                    it.city,
                    it.team,
                    problemRepository.findFirstById(it.problem)!!,
                    ageRepository.findFirstById(it.age)!!,
                    stageRepository.findFirstById(it.stage)!!,
                    it.performance,
                    it.spontan
            )
        }
        timeTableRepository.saveAll(per)
        return per
    }

    @PutMapping
    fun updatePerformance(@RequestBody performance: PerformanceDTO){
        val pToEdit = timeTableRepository.findById(performance.id).get()
        pToEdit.city = performance.city
        pToEdit.team = performance.team
        pToEdit.problem = problemRepository.findFirstById(performance.problem)!!
        pToEdit.age = ageRepository.findFirstById(performance.age)!!
        pToEdit.stage = stageRepository.findFirstById(performance.stage)!!
        pToEdit.performance = performance.performance
        pToEdit.spontan = performance.spontan
        timeTableRepository.save(pToEdit)
    }

    @DeleteMapping("{id}")
    fun delPerformance(@PathVariable id: Int){
        timeTableRepository.deleteById(id)
    }
}

data class PerformanceDTO(
        val id: Int,
        val city: String,
        val team: String,
        val problem: Int,
        val age: Int,
        val stage: Int,
        val performance: String,
        val spontan: String
)
