package odyseja.odysejapka.exceptions

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(e: EntityNotFoundException, request: HttpServletRequest): ProblemDetail {
        val response = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
        response.title = "ENTITY NOT FOUND"
        response.instance = URI.create(request.requestURI)
        return response
    }
}