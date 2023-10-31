package odyseja.odysejapka.service

import odyseja.odysejapka.domain.BreakingChangeEntity
import odyseja.odysejapka.rest.BreakingChangeController
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BreakingChangeService(private val breakingChangeRepository: BreakingChangeRepository) {
    fun getLastBreakingChange(): BreakingChangeController.BreakingChange {
        return breakingChangeRepository.findFirstByOrderByIdDesc()?.toBreakingChange()
            ?: BreakingChangeController.BreakingChange(
                version = "0.0.0"
            )
    }

    @Transactional
    fun setBreakingChange(breakingChange: BreakingChangeController.BreakingChange) {
        val breakingChangeEntity = breakingChangeRepository.findFirstByOrderByIdDesc()

        if (breakingChangeEntity == null) {
            breakingChangeRepository.save(BreakingChangeEntity(1, breakingChange.version))
            return
        }

        breakingChangeEntity.version = breakingChange.version
        breakingChangeRepository.save(breakingChangeEntity)
    }
}