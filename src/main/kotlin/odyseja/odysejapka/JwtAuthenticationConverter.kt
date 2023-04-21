package odyseja.odysejapka

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors

@Component
class JwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {
    private val ROLES_CLAIM = "https://app.odyseja.org/roles"

    override fun convert(jwt: Jwt): AbstractAuthenticationToken? {
        val authorities = extractAuthorities(jwt)
        return JwtAuthenticationToken(jwt, authorities)
    }

    private fun extractAuthorities(jwt: Jwt): Collection<SimpleGrantedAuthority> {
        val roles = jwt.getClaim<List<String>>(ROLES_CLAIM)
                ?: return Collections.emptyList()
        return roles.stream()
                .map<SimpleGrantedAuthority> { role: String -> SimpleGrantedAuthority("ROLE_$role") }
                .collect(Collectors.toList())
    }
}