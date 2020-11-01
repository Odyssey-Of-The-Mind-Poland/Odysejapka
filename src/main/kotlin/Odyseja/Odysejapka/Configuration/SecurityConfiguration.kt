package Odyseja.Odysejapka.Configuration

import Odyseja.Odysejapka.Configuration.JsonAuthFilter.JsonObjectAuthenticationFilter
import Odyseja.Odysejapka.Configuration.JsonAuthFilter.RestAuthenticationFailureHandler
import Odyseja.Odysejapka.Configuration.JsonAuthFilter.RestAuthenticationSuccessHandler
import Odyseja.Odysejapka.Service.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.sql.DataSource


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
    }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
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