package odyseja.odysejapka.form

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JudgeCountService(
    private val cityFormJudgesRepository: CityFormJudgesRepository,
    private val cityService: CityService,
) {

    @Transactional
    fun setJudgesCount(problem: Int, smallJudgesTeam: List<Int>?, bigJudgesTeam: List<Int>?) {
        smallJudgesTeam?.forEach { setJudgeCountForCity(problem, it, 1) }
        bigJudgesTeam?.forEach { setJudgeCountForCity(problem, it, 2) }
    }

    private fun setJudgeCountForCity(problem: Int, cityId: Int, judgeCount: Int) {
        val city = cityService.getCity(cityId)
        val judgeEntity = cityFormJudgesRepository.findByProblemAndCity(problem, city)
            ?: CityFormJudgesEntity.create(problem, city)
        judgeEntity.judgeCount = judgeCount
        cityFormJudgesRepository.save(judgeEntity)
    }

    fun getJudgeCountByProblemAndCity(problem: Int, cityId: Int): JudgeCountResponse {
        val city = cityService.getCity(cityId)
        val judgeEntity = cityFormJudgesRepository.findByProblemAndCity(problem, city)
            ?: CityFormJudgesEntity()
                .apply { this.problem = problem; this.city = city; this.judgeCount = 1 }
        return JudgeCountResponse(judgeEntity.judgeCount)
    }

    fun getJudgeCountByProblem(problem: Int): List<CityFormJudgesEntity?> {
        return cityFormJudgesRepository.findByProblem(problem)
    }

    fun deleteJudgeCountsByCity(cityId: Int) {
        cityFormJudgesRepository.deleteByCity(cityService.getCity(cityId))
    }
}

