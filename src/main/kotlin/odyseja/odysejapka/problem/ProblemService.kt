package odyseja.odysejapka.problem

import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val changeService: ChangeService
) {

    fun getProblemsWithNewLines(): List<ProblemEntity?> {
        // Lappka need \n to split problem names
        return problemRepository.findAll().sortedBy { it.id }.map { problem ->
            problem?.let { ProblemEntity(it.id, it.name.replace("\\n", "\n")) }
        }
    }

    fun getProblems(): List<ProblemEntity?> {
        return problemRepository.findAll().sortedBy { it.id }.map { problem ->
            problem?.let { ProblemEntity(it.id, it.name
                .replace(Regex("\\n"), "")
                .replace("\n", "")) }
        }
    }

    fun updateProblem(problemEntities: List<ProblemEntity>) {
        for (problem in problemEntities) {
            val toEdit: ProblemEntity = problemRepository.findById(problem.id).get()
            toEdit.name = problem.name
            problemRepository.save(toEdit)
        }

        changeService.updateVersion()
    }

    fun deleteProblem(problemId: Int) {
        problemRepository.deleteById(problemId)

        changeService.updateVersion()
    }
}