package odyseja.odysejapka.resources

import odyseja.odysejapka.data.model.*
import odyseja.odysejapka.repository.*
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/timeTable")
class TimeTableController(
        private val timeTableRepository: PerformanceRepository,
        private val problemRepository: ProblemRepository,
        private val stageRepository: StageRepository,
        private val ageRepository: AgeRepository,
        private val cityRepository: CityRepository
) {

    @GetMapping()
    fun getAll(): List<PerformanceDTO> {
        return timeTableRepository.findAll().map { PerformanceDTO(
                it!!.id,
                it.city.name,
                it.team,
                it.problem.id,
                it.age.id,
                it.stage.number,
                it.performance,
                it.spontan
        ) }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping()
    @ResponseBody
    fun addPerformance(@RequestBody performances: List<PerformanceDTO>): List<Performance> {
        val city: City = cityRepository.findFirstByName(performances[0].city) ?: cityRepository.save(City(0, performances[0].city))
        timeTableRepository.deleteByCity(city)
        val per: List<Performance> = performances.map {
            Performance(
                    it.id,
                    cityRepository.findFirstByName(it.city) ?: cityRepository.save(City(0, it.city)),
                    it.team,
                    problemRepository.findFirstById(it.problem)
                            ?: problemRepository.save(Problem(it.problem, "Porblem nr. " + it.problem)),
                    ageRepository.findFirstById(it.age)
                            ?: ageRepository.save(Age(it.age, "Grupa wiekowa nr. " + it.age)),
                    stageRepository.findFirstByNumberAndCity(it.stage, cityRepository.findFirstByName(it.city))
                            ?: stageRepository.save(Stage(0, it.stage, "Scena nr. " + it.stage, city)),
                    it.performance,
                    it.spontan
            )
        }
        timeTableRepository.saveAll(per)
        return per
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updatePerformance(@RequestBody performance: PerformanceDTO){
        val pToEdit = timeTableRepository.findById(performance.id).get()
        pToEdit.city = cityRepository.findFirstByName(performance.city)
                ?: cityRepository.save(City(0, performance.city))
        pToEdit.team = performance.team
        pToEdit.problem = problemRepository.findFirstById(performance.problem)
                ?: problemRepository.save(Problem(performance.problem, "Porblem nr. " + performance.problem))
        pToEdit.age = ageRepository.findFirstById(performance.age)
                ?: ageRepository.save(Age(performance.age, "Grupa wiekowa nr. " + performance.age))
        pToEdit.stage = stageRepository.findFirstById(performance.stage)
                ?: stageRepository.save(Stage(0, performance.stage, "Scena nr. " + performance.stage, pToEdit.city))
        pToEdit.performance = performance.performance
        pToEdit.spontan = performance.spontan
        timeTableRepository.save(pToEdit)
    }

    @Secured("ROLE_ADMIN")
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