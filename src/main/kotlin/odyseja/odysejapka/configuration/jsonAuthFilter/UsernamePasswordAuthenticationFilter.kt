package odyseja.odysejapka.configuration.jsonAuthFilter

import odyseja.odysejapka.data.dto.LoginCredentials
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JsonObjectAuthenticationFilter : UsernamePasswordAuthenticationFilter() {
    private val objectMapper = jacksonObjectMapper()
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        return try {
            val reader = request.reader
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val authRequest: LoginCredentials = objectMapper.readValue(sb.toString(), LoginCredentials::class.java)
            val token = UsernamePasswordAuthenticationToken(
                    authRequest.username, authRequest.password
            )
            setDetails(request, token)
            authenticationManager.authenticate(token)
        } catch (e: IOException) {
            response.status = HttpStatus.BAD_REQUEST.value()
            return null
        }
    }
}

@Component
class RestAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse,
                                         authentication: Authentication) {
        clearAuthenticationAttributes(request)
    }
}

@Component
class RestAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest?, response: HttpServletResponse?,
                                         exception: AuthenticationException?) {
        response?.status = HttpStatus.UNAUTHORIZED.value()
        super.onAuthenticationFailure(request, response, exception)
    }
}