package odyseja.odysejapka.dashboard

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt

fun extractUserId(principal: Any?): String? = when (principal) {
    is Jwt -> principal.subject
    is UserDetails -> principal.username
    is String -> principal
    else -> null
}
