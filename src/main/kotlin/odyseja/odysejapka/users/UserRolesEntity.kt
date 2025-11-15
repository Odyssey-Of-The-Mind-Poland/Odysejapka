package odyseja.odysejapka.users

import jakarta.persistence.*
import odyseja.odysejapka.roles.Role
import java.time.Instant

@Entity
@Table(name = "user_roles")
class UserRolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "user_id", nullable = false)
    var userId: String? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null

    companion object {
        fun from(userRoles: UserRoles, userId: String): List<UserRolesEntity> {
            return userRoles.roles.map { role ->
                UserRolesEntity().apply {
                    this.userId = userId
                    this.role = role
                    createdAt = Instant.now()
                }
            }
        }
    }

    fun toUserRole(): Role {
        return role ?: throw IllegalStateException("Role cannot be null")
    }
} 