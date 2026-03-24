package odyseja.odysejapka.auth

import odyseja.odysejapka.security.JwtService
import odyseja.odysejapka.users.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenResponse {
        val user = userService.verifyUserLogin(request)
        return TokenResponse(token = jwtService.generateToken(user))
    }
}
