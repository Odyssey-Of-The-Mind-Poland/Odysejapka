package odyseja.odysejapka.auth

import odyseja.odysejapka.security.JwtService
import odyseja.odysejapka.users.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val token: String
)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")

        if (user.authProvider != "local") {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This account uses Auth0 login")
        }

        if (user.password != request.password) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        return TokenResponse(token = jwtService.generateToken(user))
    }
}
