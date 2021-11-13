package odyseja.odysejapka.configuration

import odyseja.odysejapka.configuration.jsonAuthFilter.JsonObjectAuthenticationFilter
import odyseja.odysejapka.configuration.jsonAuthFilter.RestAuthenticationFailureHandler
import odyseja.odysejapka.configuration.jsonAuthFilter.RestAuthenticationSuccessHandler
import odyseja.odysejapka.service.UserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@EnableWebSecurity
class SecurityConfiguration(
        private val authenticationSuccessHandler: RestAuthenticationSuccessHandler,
        private val authenticationFailureHandler: RestAuthenticationFailureHandler,
        private val userService: UserDetailsService
) : WebSecurityConfigurerAdapter(){

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable().formLogin()
                .and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .cors()
                .and()
    }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowCredentials = true
        configuration.applyPermitDefaultValues()
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationFilter(): JsonObjectAuthenticationFilter? {
        val filter = JsonObjectAuthenticationFilter()
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        filter.setAuthenticationFailureHandler(authenticationFailureHandler)
        filter.setAuthenticationManager(super.authenticationManager())
        return filter
    }
}