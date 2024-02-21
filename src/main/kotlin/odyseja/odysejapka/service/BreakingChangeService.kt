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

    fun shouldUpdate(breakingChange: BreakingChangeController.BreakingChange): Boolean {
        val breakingChangeVersionParts = getLastBreakingChange().version.split(".").map { it.toInt() }
        val currentVersionParts = breakingChange.version.split(".").map { it.toInt() }

        val maxLength = maxOf(breakingChangeVersionParts.size, currentVersionParts.size)

        for (i in 0 until maxLength) {
            val currentPart = currentVersionParts.getOrElse(i) { 0 }
            val breakingChangePart = breakingChangeVersionParts.getOrElse(i) { 0 }

            if (currentPart < breakingChangePart) {
                return true
            } else if (currentPart > breakingChangePart) {
                return false
            }
        }
        return false
    }
}