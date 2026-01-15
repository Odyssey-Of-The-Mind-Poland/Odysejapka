package odyseja.odysejapka.form

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.city.CityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JudgeCountService(
    private val formProblemRepository: FormProblemRepository,
    private val cityRepository: CityRepository,
) {

    @Transactional
    fun setJudgesCount(problem: Int, smallJudgesTeam: List<Int>?, bigJudgesTeam: List<Int>?) {
        smallJudgesTeam?.forEach { setJudgeCountForCity(problem, it, 1) }
        bigJudgesTeam?.forEach { setJudgeCountForCity(problem, it, 2) }
    }

    private fun setJudgeCountForCity(problem: Int, cityId: Int, judgeCount: Int) {
        val city = cityRepository.findFirstById(cityId)
        val problemEntity = formProblemRepository.findByProblemAndCity(problem, city)
            ?: FormProblemEntity.create(problem, city)
        problemEntity.judgeCount = judgeCount
        formProblemRepository.save(problemEntity)
    }

    fun getJudgeCount(problem: Int, cityId: Int): JudgeCountResponse {
        val city = cityRepository.findFirstById(cityId)
        val problemEntity = formProblemRepository.findByProblemAndCity(problem, city)
            ?: throw EntityNotFoundException("No judge count set for problem $problem and city $cityId")
        return JudgeCountResponse(judgeCount = problemEntity.judgeCount)
    }
}

