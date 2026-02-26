package odyseja.odysejapka.users

import jakarta.persistence.*
import odyseja.odysejapka.roles.Role
import java.time.Instant

@Entity
@Table(name = "users")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: String? = null

    @Column(name = "password")
    var password: String? = null

    @Column(name = "auth_provider", nullable = true)
    var authProvider: String = "auth0"

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null

    companion object {
        fun from(user: CreateUserRequest): UserEntity {
            return UserEntity().apply {
                name = user.username
                email = user.email
                userId = user.userId
                createdAt = Instant.now()
            }
        }

        fun forLocalAuth(name: String, email: String, password: String): UserEntity {
            return UserEntity().apply {
                this.name = name
                this.email = email
                this.userId = java.util.UUID.randomUUID().toString()
                this.password = password
                this.authProvider = "local"
                this.createdAt = Instant.now()
            }
        }
    }

    fun toUser(userRoles: List<Role> = emptyList()): User {
        return User(
            id = id ?: 0L,
            username = name ?: "",
            email = email ?: "",
            userId = userId ?: "",
            roles = userRoles
        )
    }
}