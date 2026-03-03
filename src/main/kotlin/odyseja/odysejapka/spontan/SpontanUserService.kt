package odyseja.odysejapka.spontan

import odyseja.odysejapka.roles.Role
import odyseja.odysejapka.users.UserEntity
import odyseja.odysejapka.users.UserRepository
import odyseja.odysejapka.users.UserRoles
import odyseja.odysejapka.users.UserRolesRepository
import odyseja.odysejapka.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
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
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userRolesRepository: UserRolesRepository,
    private val spontanGroupAssignmentRepository: SpontanGroupAssignmentRepository
) {
    private val logger = LoggerFactory.getLogger(SpontanUserService::class.java)

    @Transactional
    fun createSpontanUser(cityId: Int, username: String): SpontanUserCredentials {
        val slug = slugify(username)
        val email = "$slug@spontan.odyseja.local"
        val password = generatePassword()

        val userEntity = UserEntity.forLocalAuth(username, email, password)
        val savedUser = userRepository.save(userEntity)

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
    fun getSpontanUsers(cityId: Int): List<SpontanUserInfo> {
        return spontanUserRepository.findAllByCityId(cityId).mapNotNull { spontanUser ->
            val user = userRepository.findById(spontanUser.userId).orElse(null) ?: return@mapNotNull null
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
        val spontanUsers = spontanUserRepository.findAllByCityId(cityId)
        val spontanUser = spontanUsers.find { it.userId == userId } ?: return null
        val user = userRepository.findById(spontanUser.userId).orElse(null) ?: return null
        return SpontanUserCredentials(
            email = user.email ?: return null,
            password = user.password ?: return null
        )
    }

    @Transactional
    fun deleteSpontanUser(cityId: Int, userId: Long) {
        val spontanUsers = spontanUserRepository.findAllByCityId(cityId)
        val spontanUser = spontanUsers.find { it.userId == userId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Spontan user not found")

        // Clear group assignments referencing this spontan user
        val assignments = spontanGroupAssignmentRepository.findByCityId(cityId)
        for (assignment in assignments) {
            if (assignment.spontanUser?.id == spontanUser.id) {
                assignment.spontanUser = null
                spontanGroupAssignmentRepository.save(assignment)
            }
        }

        spontanUserRepository.delete(spontanUser)

        val user = userRepository.findById(spontanUser.userId).orElse(null)
        if (user != null) {
            userRolesRepository.deleteByUserId(user.userId!!)
            userRepository.delete(user)
        }

        logger.info("Deleted spontan user {} for city {}", userId, cityId)
    }

    @Transactional
    fun assignUserToGroup(assignmentId: Long, spontanUserId: Long?) {
        val assignment = spontanGroupAssignmentRepository.findById(assignmentId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Group assignment not found")

        assignment.spontanUser = spontanUserId?.let {
            spontanUserRepository.findById(it).orElse(null)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Spontan user not found")
        }

        spontanGroupAssignmentRepository.save(assignment)
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
