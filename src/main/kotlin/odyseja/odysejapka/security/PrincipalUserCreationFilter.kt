package odyseja.odysejapka.security

import com.auth0.client.auth.AuthAPI
import com.auth0.json.auth.UserInfo
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.filter.GenericFilterBean
import odyseja.odysejapka.users.CreateUserRequest
import odyseja.odysejapka.users.UserService
import odyseja.odysejapka.users.User

class PrincipalUserCreationFilter(
    private val userService: UserService,
    private val auth0Domain: String,
    private val auth0ClientId: String
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            createUser(authentication, request as HttpServletRequest)
        }
        chain.doFilter(request, response)
    }

    private fun createUser(authentication: Authentication, request: HttpServletRequest) {
        val principal = authentication.principal
        val userId = getUserId(principal)
        if (userId == null) {
            return
        }

        if (userAlreadyCreated(userId)) {
            return
        }

        val accessToken = extractAccessToken(request)

        if (accessToken == null) {
            logger.warn("Access token not found in request headers for user: $userId")
            return
        }

        val userInfo = fetchAuth0UserInfo(accessToken, userId)
        userService.createUser(userInfo)
    }

    private fun userAlreadyCreated(userId: String): Boolean = userService.getUserByUserId(userId) != null

    private fun getUserId(principal: Any): String? = when (principal) {
        is Jwt -> principal.subject
        is UserDetails -> principal.username
        is String -> principal
        else -> null
    }

    private fun extractAccessToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.removePrefix("Bearer ").trim()
        }
        return null
    }

    private fun fetchAuth0UserInfo(accessToken: String, userId: String): CreateUserRequest {
        val api = AuthAPI.Builder(auth0Domain, auth0ClientId)
            .build()
        val userInfo: UserInfo = api.userInfo(accessToken).execute().body
        val name = userInfo.values["name"] as? String
        val email = userInfo.values["email"] as? String
        return CreateUserRequest(
            username = name ?: userId,
            email = email ?: "",
            userId = userId
        )
    }
} 