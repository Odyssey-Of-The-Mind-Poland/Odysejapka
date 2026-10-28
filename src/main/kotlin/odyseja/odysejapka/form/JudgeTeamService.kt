package odyseja.odysejapka.form

import org.springframework.transaction.annotation.Transactional
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service

@Service
class JudgeTeamService(
    private val judgeTeamRepository: JudgeTeamRepository,
    private val cityService: CityService,
    private val changeService: ChangeService,
    private val problemFormService: ProblemFormService
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

    fun getJudgeTeamsByProblem(problem: Int): List<JudgeTeam> {
        val allCities = cityService.getCities()
        val judgeTeams = mutableListOf<JudgeTeam>()
        allCities.forEach {
            val judgeTeam = getJudgeTeamEntityByProblemAndCity(problem, it?.id ?: return judgeTeams)
            judgeTeams.add(judgeTeam.toJudgeTeam())
        }

        return judgeTeams
    }

    @Transactional
    fun setJudgeTeamForCity(problem: Int, cityId: Int, judgeCount: JudgeCount) {
        val judgeTeam = getJudgeTeamEntityByProblemAndCity(problem, cityId)
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
        if (judgeTeams.isEmpty()) return
        judgeTeams.forEach {
            setJudgeTeamForCity(problem, it.cityId, it.judgeCount)
        }
    }

    @Transactional
    fun deleteJudgeTeamsByCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        judgeTeamRepository.deleteByCity(city)
    }

    private fun getJudgeTeamEntityByProblemAndCity(problem: Int, cityId: Int): JudgeTeamEntity {
        val city = cityService.getCity(cityId)
        val problemForm = problemFormService.findByProblem(problem)
        val judgeTeam = judgeTeamRepository.findByProblemFormAndCity(problemForm, city)
            ?: JudgeTeamEntity.create(problemForm, city)
        return judgeTeam
    }
}