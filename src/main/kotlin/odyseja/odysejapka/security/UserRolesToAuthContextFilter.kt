package odyseja.odysejapka.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.filter.GenericFilterBean
import odyseja.odysejapka.users.UserService

class UserRolesToAuthContextFilter(private val userService: UserService) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication
        if (authentication != null && authentication.isAuthenticated) {
            setUserRoles(authentication, context)
        }
        chain.doFilter(request, response)
    }

    private fun setUserRoles(
        authentication: Authentication,
        context: SecurityContext
    ) {
        val userId = getUserId(authentication.principal)
        if (userId == null) {
            logger.warn("No user ID provided. Using default user context")
            return
        }

        val user = userService.getUserByUserId(userId)
        if (user == null || user.roles.isEmpty()) {
            logger.warn("User with ID $userId not found or has no roles. Using default user context")
            return
        }

        val authorities: List<GrantedAuthority> = user.roles.map { SimpleGrantedAuthority("ROLE_$it") }
        val newAuth = UsernamePasswordAuthenticationToken(
            authentication.principal,
            authentication.credentials,
            authorities
        )
        newAuth.details = authentication.details
        context.authentication = newAuth
    }

    private fun getUserId(principal: Any): String? = when (principal) {
        is Jwt -> principal.subject
        is UserDetails -> principal.username
        is String -> principal
        else -> null
    }
} 