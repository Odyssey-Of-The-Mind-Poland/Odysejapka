package odyseja.odysejapka.stage

import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.users.UserEntity
import odyseja.odysejapka.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer

data class StageUserCredentials(
    val email: String,
    val password: String
)

@Service
class StageUserService(
    private val stageUserRepository: StageUserRepository,
    private val userRepository: UserRepository,
    private val cityService: CityService
) {
    private val logger = LoggerFactory.getLogger(StageUserService::class.java)

    @Transactional
    fun createStageUsers(cityId: Int, stages: Set<Int>) {
        val city = cityService.getCity(cityId)
        val citySlug = slugify(city.name)
        deleteStageUsersByCity(cityId)

        for (stage in stages) {
            val existing = getStageUserOrNull(cityId, stage)
            if (existing != null) {
                logger.info("Stage user already exists for city {} stage {}", cityId, stage)
                continue
            }

            val email = "$citySlug-scena-$stage@odyseja.local"
            val password = generatePassword()
            val displayName = "${city.name} Scena $stage"

            val userEntity = UserEntity.forLocalAuth(displayName, email, password)
            val savedUser = userRepository.save(userEntity)

            val stageUser = StageUserEntity(
                cityId = cityId,
                stage = stage,
                userId = savedUser.id!!
            )
            stageUserRepository.save(stageUser)

            logger.info("Created stage user {} for city {} stage {}", email, cityId, stage)
        }
    }

    @Transactional(readOnly = true)
    fun getCredentials(cityId: Int, stage: Int): StageUserCredentials? {
        val stageUser = getStageUserOrNull(cityId, stage) ?: return null
        val user = userRepository.findById(stageUser.userId).orElse(null) ?: return null
        return StageUserCredentials(
            email = user.email ?: return null,
            password = user.password ?: return null
        )
    }

    fun getStageUserOrNullByUserId(userId: Long): StageUserEntity? {
        return stageUserRepository.findByUserId(userId)
    }

    @Transactional
    fun deleteStageUsersByCity(cityId: Int) {
        stageUserRepository.deleteAllByCityId(cityId)
    }

    private fun getStageUserOrNull(cityId: Int, stage: Int): StageUserEntity? {
        return stageUserRepository.findByCityIdAndStage(cityId, stage)
    }

    private fun generatePassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789"
        return (1..12).map { chars.random() }.joinToString("")
    }

    private fun slugify(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
    }
}
