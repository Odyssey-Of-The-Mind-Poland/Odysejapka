package odyseja.odysejapka.users

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.auth.LoginRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import odyseja.odysejapka.roles.Role
import org.apache.http.auth.InvalidCredentialsException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRolesRepository: UserRolesRepository
) {
    @Transactional
    fun addUserEntity(user: UserEntity): UserEntity {
        return userRepository.save(user)
    }

    @Transactional
    fun createUser(user: CreateUserRequest): User {
        val entity = addUserEntity(UserEntity.from(user))
        if (isFirstUser()) {
            assignRolesToUser(
                UserRoles(
                    userId = entity.id ?: throw IllegalStateException("User ID cannot be null"),
                    roles = listOf(Role.ADMINISTRATOR)
                )
            )
        }
        return entity.toUser()
    }

    private fun isFirstUser(): Boolean = userRepository.findAll().count() == 1

    @Transactional(readOnly = true)
    fun getUserByUserId(userId: String): User? {
        val entity = getUserEntityOrNullByUserId(userId)
        if (entity != null) {
            val roles = userRolesRepository.findByUserId(userId).map { it.toUserRole() }
            return entity.toUser(roles)
        }
        return null
    }

    @Transactional(readOnly = true)
    fun getUser(id: Long): User? {
        val entity = getUserEntity(id)
        return entity.let { userEntity ->
            val userId = userEntity.userId ?: return null
            val roles = userRolesRepository.findByUserId(userId).map { it.toUserRole() }
            userEntity.toUser(roles)
        }
    }

    @Transactional(readOnly = true)
    fun listUsers(): List<User> {
        return userRepository.findAll().map { entity ->
            val roles =
                entity.userId?.let { userRolesRepository.findByUserId(it).map { it.toUserRole() } } ?: emptyList()
            entity.toUser(roles)
        }
    }

    @Transactional(readOnly = true)
    fun verifyUserLogin(request: LoginRequest): UserEntity {
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException("Nieprawidłowe dane uwierzytelniające")

        if (user.authProvider != "local") {
            throw InvalidCredentialsException("To konto używa autoryzacji Auth0")
        }

        if (user.password != request.password) {
            throw InvalidCredentialsException("Nieprawidłowe dane uwierzytelniające")
        }

        return user
    }

    @Transactional
    fun assignRolesToUser(userRoles: UserRoles): UserRoles {
        val user = getUserEntity(userRoles.userId)
        userRolesRepository.deleteByUserId(user.userId!!)

        val roleEntities = UserRolesEntity.from(userRoles, user.userId!!)
        userRolesRepository.saveAll(roleEntities)

        return userRoles
    }

    @Transactional(readOnly = true)
    fun getUserRoles(id: Long): UserRoles {
        val user = getUserEntity(id)
        val roleEntities = userRolesRepository.findByUserId(user.userId!!)
        val roles = roleEntities.map { it.toUserRole() }
        return UserRoles(id, roles)
    }

    @Transactional(readOnly = true)
    fun getUserEntity(id: Long): UserEntity {
        return userRepository.findFirstById(id)
            ?: throw EntityNotFoundException("Nie znaleziono użytkownika o ID $id")
    }

    @Transactional(readOnly = true)
    fun getUserEntityOrNull(id: Long): UserEntity? {
        return userRepository.findFirstById(id)
    }

    @Transactional(readOnly = true)
    fun getUserEntityByUserId(userId: String): UserEntity {
        return userRepository.findByUserId(userId)
            ?: throw EntityNotFoundException("Nie znaleziono użytkownika o UserID $userId")
    }

    @Transactional(readOnly = true)
    fun getUserEntityOrNullByUserId(userId: String): UserEntity? {
        return userRepository.findByUserId(userId)
    }

    @Transactional
    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }
}
