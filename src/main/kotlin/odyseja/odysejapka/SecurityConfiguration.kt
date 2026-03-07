package odyseja.odysejapka

import odyseja.odysejapka.security.JwtService
import odyseja.odysejapka.security.PrincipalUserCreationFilter
import odyseja.odysejapka.security.UserRolesToAuthContextFilter
import odyseja.odysejapka.users.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import jakarta.servlet.http.HttpServletRequest

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val userService: UserService,
    private val jwtService: JwtService,
    @Value("\${auth0.domain}") private val auth0Domain: String,
    @Value("\${auth0.clientId}") private val auth0ClientId: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val auth0IssuerUri: String
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf(
                "http://localhost:5172",
                "http://localhost:5173",
                "http://grzybek.snet.ovh:3000",
                "https:/panel.odyseja.org",
                "https:/app.odyseja.org"
            )
            allowCredentials = true
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Location")
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }

    @Bean
    fun multiIssuerAuthenticationManagerResolver(): AuthenticationManagerResolver<HttpServletRequest> {
        val auth0Decoder: JwtDecoder = JwtDecoders.fromIssuerLocation(auth0IssuerUri)
        val auth0Manager = JwtAuthenticationProvider(auth0Decoder)

        val localDecoder = NimbusJwtDecoder.withSecretKey(jwtService.getSigningKey())
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
        val localManager = JwtAuthenticationProvider(localDecoder)

        val managers: Map<String, AuthenticationManager> = mapOf(
            auth0IssuerUri to AuthenticationManager { auth0Manager.authenticate(it) },
            JwtService.ISSUER to AuthenticationManager { localManager.authenticate(it) }
        )

        return AuthenticationManagerResolver { request ->
            // Extract token from Authorization header and peek at the issuer claim
            val token = request.getHeader("Authorization")
                ?.removePrefix("Bearer ")
                ?.trim()

            val issuer = token?.let { extractIssuer(it) } ?: auth0IssuerUri
            managers[issuer]
                ?: throw org.springframework.security.oauth2.jwt.JwtException("Unknown issuer: $issuer")
        }
    }

    private fun extractIssuer(token: String): String? {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val decoded = String(java.util.Base64.getUrlDecoder().decode(payload))
            val mapper = com.fasterxml.jackson.databind.ObjectMapper()
            val claims = mapper.readTree(decoded)
            claims.get("iss")?.asText()
        } catch (e: Exception) {
            null
        }
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        issuerResolver: AuthenticationManagerResolver<HttpServletRequest>
    ): SecurityFilterChain {
        http
            .cors { /* uses the bean above */ }
            .csrf { it.disable() }

            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/version").permitAll()
                    .requestMatchers("/ws/**").permitAll()
                    .anyRequest().permitAll()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.authenticationManagerResolver(issuerResolver)
            }

            .addFilterAfter(
                PrincipalUserCreationFilter(userService, auth0Domain, auth0ClientId),
                BearerTokenAuthenticationFilter::class.java
            )
            .addFilterAfter(
                UserRolesToAuthContextFilter(userService),
                PrincipalUserCreationFilter::class.java
            )

        return http.build()
    }
}
