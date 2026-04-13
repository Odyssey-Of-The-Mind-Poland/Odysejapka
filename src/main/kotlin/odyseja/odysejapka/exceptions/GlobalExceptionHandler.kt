package odyseja.odysejapka.exceptions

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.apache.http.auth.InvalidCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException, request: HttpServletRequest): ProblemDetail {
        val response = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            e.message
        )
        response.apply {
            title = "ILLEGAL ARGUMENT"
            instance = URI.create(request.requestURI)
        }
        return response
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException, request: HttpServletRequest): ProblemDetail {
        val response = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            e.message
        )
        response.apply {
            title = "ILLEGAL STATE"
            instance = URI.create(request.requestURI)
        }
        return response
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(e: EntityNotFoundException, request: HttpServletRequest): ProblemDetail {
        val response = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            e.message
        )
        response.apply {
            title = "ENTITY NOT FOUND"
            instance = URI.create(request.requestURI)
        }
        return response
    }

    @ExceptionHandler(NoAccessException::class)
    fun handleNoAccess(e: NoAccessException, request: HttpServletRequest): ProblemDetail {
        val response = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            e.message
        )
        response.apply {
            title = "NO ACCESS"
            instance = URI.create(request.requestURI)
        }
        return response
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(e: InvalidCredentialsException, request: HttpServletRequest): ProblemDetail {
        val response = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            e.message
        )
        response.apply {
            title = "INVALID CREDENTIALS"
            instance = URI.create(request.requestURI)
        }
        return response
    }
}