package odyseja.odysejapka.spontan

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.roles.Role
import odyseja.odysejapka.users.UserEntity
import odyseja.odysejapka.users.UserRoles
import odyseja.odysejapka.users.UserRolesRepository
import odyseja.odysejapka.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer

data class SpontanUserCredentials(
    val email: String,
    val password: String
)

data class SpontanUserInfo(
    val id: Long,
    val userId: Long,
    val name: String,
    val email: String
)

@Service
class SpontanUserService(
    private val spontanUserRepository: SpontanUserRepository,
    private val userService: UserService,
    private val userRolesRepository: UserRolesRepository,
    private val spontanGroupAssignmentService: SpontanGroupAssignmentService
) {
    private val logger = LoggerFactory.getLogger(SpontanUserService::class.java)

    @Transactional
    fun createSpontanUser(cityId: Int, username: String): SpontanUserCredentials {
        val slug = slugify(username)
        val email = "$slug@spontan.odyseja.local"
        val password = generatePassword()

        val userEntity = UserEntity.forLocalAuth(username, email, password)
        val savedUser = userService.addUserEntity(userEntity)

        userService.assignRolesToUser(
            UserRoles(
                userId = savedUser.id!!,
                roles = listOf(Role.SPONTAN)
            )
        )

        val spontanUser = SpontanUserEntity(
            cityId = cityId,
            userId = savedUser.id!!
        )
        spontanUserRepository.save(spontanUser)

        logger.info("Created spontan user {} for city {}", email, cityId)

        return SpontanUserCredentials(email = email, password = password)
    }

    @Transactional(readOnly = true)
    fun getSpontanUsersInfo(cityId: Int): List<SpontanUserInfo> {
        return getSpontanUsersByCity(cityId).mapNotNull { spontanUser ->
            val user = userService.getUserEntityOrNull(spontanUser.userId) ?: return@mapNotNull null
            SpontanUserInfo(
                id = spontanUser.id!!,
                userId = spontanUser.userId,
                name = user.name ?: "",
                email = user.email ?: ""
            )
        }
    }

    @Transactional(readOnly = true)
    fun getCredentials(cityId: Int, userId: Long): SpontanUserCredentials? {
        val spontanUsers = getSpontanUsersByCity(cityId)
        val spontanUser = spontanUsers.find { it.userId == userId } ?: return null
        val user = userService.getUserEntityOrNull(spontanUser.userId) ?: return null
        return SpontanUserCredentials(
            email = user.email ?: return null,
            password = user.password ?: return null
        )
    }

    @Transactional
    fun deleteSpontanUser(cityId: Int, userId: Long) {
        val spontanUser = getSpontanUserByUserId(userId)

        // Clear group assignments referencing this spontan user
        val assignments = spontanGroupAssignmentService.getAssignmentEntitiesByCity(cityId)
        for (assignment in assignments) {
            if (assignment.spontanUser?.id == spontanUser.id) {
                assignment.spontanUser = null
                spontanGroupAssignmentService.addAssignmentEntity(assignment)
            }
        }

        spontanUserRepository.delete(spontanUser)

        val user = userService.getUserEntityOrNull(spontanUser.userId)
        if (user != null) {
            userRolesRepository.deleteByUserId(user.userId!!)
            userService.deleteUser(user.id!!)
        }

        logger.info("Deleted spontan user {} for city {}", userId, cityId)
    }

    @Transactional
    fun assignUserToGroup(assignmentId: Long, spontanUserId: Long?) {
        val assignment = spontanGroupAssignmentService.getAssignmentEntityById(assignmentId)

        assignment.spontanUser = spontanUserId?.let {
            getSpontanUser(it)
        }

        spontanGroupAssignmentService.addAssignmentEntity(assignment)
    }

    fun getSpontanUsersByCity(cityId: Int): List<SpontanUserEntity> {
        return spontanUserRepository.findAllByCityId(cityId)
    }

    fun getSpontanUser(spontanUserId: Long): SpontanUserEntity {
        return spontanUserRepository.findFirstById(spontanUserId)
            ?: throw EntityNotFoundException("Nie znaleziono użytkownika o Spontan-ID $spontanUserId")
    }

    fun getSpontanUserByUserId(userId: Long): SpontanUserEntity {
        return spontanUserRepository.findByUserId(userId)
            ?: throw EntityNotFoundException ("Nie znaleziono użytkownika o ID $userId w bazie danych spontanów")
    }

    fun getSpontanUserOrNullByUserId(userId: Long): SpontanUserEntity? {
        return spontanUserRepository.findByUserId(userId)
    }

    @Transactional
    fun deleteSpontanUsersByCity(cityId: Int) {
        val userIds = getSpontanUsersByCity(cityId)
            .map { it.userId }

        userIds.forEach { userId ->
            deleteSpontanUser(cityId, userId)
        }
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
