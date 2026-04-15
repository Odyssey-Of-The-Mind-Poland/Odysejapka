package odyseja.odysejapka.exceptions

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.apache.http.auth.InvalidCredentialsException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e, request,
            HttpStatus.BAD_REQUEST,
            "ILLEGAL ARGUMENT"
        )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e, request,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ILLEGAL STATE"
        )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(e: EntityNotFoundException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e, request,
            HttpStatus.NOT_FOUND,
            "ENTITY NOT FOUND"
        )
    }

    @ExceptionHandler(NoAccessException::class)
    fun handleNoAccess(e: NoAccessException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e, request,
            HttpStatus.FORBIDDEN,
            "NO ACCESS"
        )
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(e: InvalidCredentialsException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e, request,
            HttpStatus.UNAUTHORIZED,
            "INVALID CREDENTIALS"
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(e: DataIntegrityViolationException, request: HttpServletRequest): ProblemDetail {
        return createProblemDetail(
            e, request,
            HttpStatus.CONFLICT,
            "DATA INTEGRITY VIOLATION"
        )
    }

    private fun createProblemDetail(exception: Exception,
                                    request: HttpServletRequest,
                                    status: HttpStatus,
                                    title: String): ProblemDetail {
        val response = ProblemDetail.forStatus(status)
        response.apply {
            this.title = title
            this.instance = URI.create(request.requestURI)
            this.detail = exception.message
        }
        return response
    }
}