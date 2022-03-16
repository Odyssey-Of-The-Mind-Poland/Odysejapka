package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ProblemEntity
import odyseja.odysejapka.port.ChangeUseCase
import odyseja.odysejapka.port.ProblemUseCase
import org.springframework.stereotype.Service

@Service
internal class ProblemService(
  private val problemRepository: ProblemRepository,
  private val changeUseCase: ChangeUseCase
) : ProblemUseCase {

  override fun getProblems(): MutableIterable<ProblemEntity?> {
    return problemRepository.findAll()
  }

  override fun updateProblem(problemEntities: List<ProblemEntity>) {
    for (problem in problemEntities) {
      val toEdit: ProblemEntity = problemRepository.findById(problem.id).get()
      toEdit.name = problem.name
      problemRepository.save(toEdit)
    }

    changeUseCase.updateVersion()
  }

  override fun deleteProblem(problemId: Int) {
    problemRepository.deleteById(problemId)

    changeUseCase.updateVersion()
  }
}