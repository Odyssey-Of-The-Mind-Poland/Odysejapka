package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ProblemEntity
import org.springframework.stereotype.Service

@Service
class ProblemService(
  private val problemRepository: ProblemRepository,
  private val changeService: ChangeService
) {

  fun getProblems(): MutableIterable<ProblemEntity?> {
    return problemRepository.findAll()
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