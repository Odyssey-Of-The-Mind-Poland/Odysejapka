package odyseja.odysejapka.breaking.change

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BreakingChangeService(private val breakingChangeRepository: BreakingChangeRepository) {
    fun getLastBreakingChange(): BreakingChange {
        return breakingChangeRepository.findFirstByOrderByIdDesc()?.toBreakingChange()
            ?: BreakingChange(
                version = "0.0.0"
            )
    }

    @Transactional
    fun setBreakingChange(breakingChange: BreakingChange) {
        breakingChange.validate()
        val breakingChangeEntity = breakingChangeRepository.findFirstByOrderByIdDesc()

        if (breakingChangeEntity == null) {
            breakingChangeRepository.save(BreakingChangeEntity(1, breakingChange.version))
            return
        }

        breakingChangeEntity.version = breakingChange.version
        breakingChangeRepository.save(breakingChangeEntity)
    }

    fun shouldUpdate(currentVersion: String): Boolean {
        BreakingChange(currentVersion).validate()
        val currentVersionFormatted = currentVersion.replace("+", ".")
        val lastBreakingChangeFormatted = getLastBreakingChange().version.replace("+", ".")

        val breakingChangeVersionParts = lastBreakingChangeFormatted.split(".").map { it.toInt() }
        val currentVersionParts = currentVersionFormatted.split(".").map { it.toInt() }

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