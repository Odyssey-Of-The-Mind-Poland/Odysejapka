package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ProblemEntity
import odyseja.odysejapka.port.ProblemUseCase
import org.springframework.stereotype.Service

@Service
internal class ProblemService(private val problemRepository: ProblemRepository) : ProblemUseCase {

  override fun getProblems(): MutableIterable<ProblemEntity?> {
    return problemRepository.findAll()
  }

  override fun updateProblem(problemEntities: List<ProblemEntity>) {
    for (problem in problemEntities){
      val toEdit: ProblemEntity = problemRepository.findById(problem.id).get()
      toEdit.name = problem.name
      problemRepository.save(toEdit)
    }
  }

  override fun deleteProblem(problemId: Int) {
    problemRepository.deleteById(problemId)
  }
}