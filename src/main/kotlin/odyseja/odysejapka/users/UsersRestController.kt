package odyseja.odysejapka.users

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import odyseja.odysejapka.roles.Role

@RestController
@RequestMapping("/api/v1/users")
class UsersRestController(private val userService: UserService?) {

    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    fun getUsers(): List<User> {
        return userService!!.listUsers()
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    fun assignRolesToUser(
        @PathVariable userId: Long,
        @RequestBody roles: List<Role>
    ): UserRoles {
        val userRoles = UserRoles(userId, roles)
        return userService!!.assignRolesToUser(userRoles)
    }

    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    fun getUserRoles(@PathVariable userId: Long): UserRoles {
        return userService!!.getUserRoles(userId)
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    fun getUser(@PathVariable userId: Long): User? {
        return userService!!.getUser(userId)
    }

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal principal: Any?): User? {
        val userId = when (principal) {
            is Jwt -> principal.subject
            is org.springframework.security.core.userdetails.UserDetails -> principal.username
            is String -> principal
            else -> null
        }
        return userId?.let { userService?.getUserByUserId(it) }
    }
} 