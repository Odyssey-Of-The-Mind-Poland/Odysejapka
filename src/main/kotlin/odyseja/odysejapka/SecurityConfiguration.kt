package odyseja.odysejapka

import odyseja.odysejapka.security.PrincipalUserCreationFilter
import odyseja.odysejapka.security.UserRolesToAuthContextFilter
import odyseja.odysejapka.users.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val userService: UserService,
    @Value("\${auth0.domain}") private val auth0Domain: String,
    @Value("\${auth0.clientId}") private val auth0ClientId: String
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("http://localhost:5172", "http://localhost:5173", "http://grzybek.snet.ovh:3000")
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
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { /* uses the bean above */ }
            .csrf { it.disable() }

            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().permitAll()
            }
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }

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
