package odyseja.odysejapka.exceptions

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.apache.http.auth.InvalidCredentialsException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.BAD_REQUEST,
            "ILLEGAL ARGUMENT"
        )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ILLEGAL STATE"
        )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(e: EntityNotFoundException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.NOT_FOUND,
            "ENTITY NOT FOUND"
        )
    }

    @ExceptionHandler(NoAccessException::class)
    fun handleNoAccess(e: NoAccessException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.FORBIDDEN,
            "NO ACCESS"
        )
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(e: InvalidCredentialsException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.UNAUTHORIZED,
            "INVALID CREDENTIALS"
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(e: DataIntegrityViolationException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.CONFLICT,
            "DATA INTEGRITY VIOLATION"
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e.message, request,
            HttpStatus.BAD_REQUEST,
            "ILLEGAL ARGUMENT"
        )
    }

    private fun createProblemDetail(
        message: String?,
        request: HttpServletRequest,
        status: HttpStatus,
        title: String
    ): ProblemDetail {
        val response = ProblemDetail.forStatus(status)
        response.apply {
            this.title = title
            this.instance = URI.create(request.requestURI)
            this.detail = message ?: "Wystąpił błąd"
        }
        return response
    }
}