package odyseja.odysejapka.problem

import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service

@Service
class ProblemService(
  private val problemRepository: ProblemRepository,
  private val changeService: ChangeService
) {

  fun getProblems(): List<ProblemEntity?> {
    return problemRepository.findAll().sortedBy { it.id }
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