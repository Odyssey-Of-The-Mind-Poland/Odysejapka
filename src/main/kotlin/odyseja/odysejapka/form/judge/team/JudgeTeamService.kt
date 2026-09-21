package odyseja.odysejapka.form.judge.team

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.form.JudgeCount
import org.springframework.stereotype.Service

@Service
class JudgeTeamService(
    private val judgeTeamRepository: JudgeTeamRepository,
    private val cityService: CityService,
    private val changeService: ChangeService
) {
    fun getJudgeCountByProblemAndCity(problem: Int, cityId: Int): JudgeCount {
        val judgeTeam = getJudgeTeamEntityByProblemAndCity(problem, cityId)
        val judgeCount = JudgeCount(
            judgeTeam.judgeCountDtA,
            judgeTeam.judgeCountDtB,
            judgeTeam.judgeCountStyle
        )
        return judgeCount
    }

    fun getJudgeTeamsByProblem(problem: Int): List<JudgeTeam?> {
        val judgeTeams = judgeTeamRepository.findByProblem(problem)
        return judgeTeams.mapNotNull {
            it?.toJudgeTeam()
        }
    }

    @Transactional
    fun setJudgeTeamForCity(problem: Int, cityId: Int, judgeCount: JudgeCount) {
        val city = cityService.getCity(cityId)
        val judgeTeam = JudgeTeamEntity.create(problem, city)
        judgeTeam.apply {
            judgeCountDtA = judgeCount.dtA
            judgeCountDtB = judgeCount.dtB
            judgeCountStyle = judgeCount.style
        }
        judgeTeamRepository.save(judgeTeam)
        changeService.updateVersion()
    }

    @Transactional
    fun setJudgeTeamsForProblem(problem: Int, judgeTeams: List<JudgeTeam>) {
        judgeTeams.forEach {
            setJudgeTeamForCity(problem, it.cityId, it.judgeCount)
        }
    }

    @Transactional
    fun deleteJudgeTeam(problem: Int, cityId: Int) {
        val judgeTeam = getJudgeTeamEntityByProblemAndCity(problem, cityId)
        judgeTeamRepository.delete(judgeTeam)
        changeService.updateVersion()
    }

    @Transactional
    fun deleteJudgeTeamsForCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        judgeTeamRepository.deleteByCity(city)
    }

    private fun getJudgeTeamEntityByProblemAndCity(problem: Int, cityId: Int): JudgeTeamEntity {
        val city = cityService.getCity(cityId)
        val judgeTeam = judgeTeamRepository.findByProblemAndCity(problem, city)
            ?: throw EntityNotFoundException("Problem $problem nie ma przypisanej liczby sędziów na konkursie $city.")
        return judgeTeam
    }
}