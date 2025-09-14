package odyseja.odysejapka.qr

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/lappka/download")
internal class MobileAppRedirectController(
    private val qrService: QrService
) {
    @GetMapping
    fun detectDevice(request: HttpServletRequest, response: HttpServletResponse) {
        val userAgent = request.getHeader("User-Agent")
        val deviceType = qrService.getDeviceType(userAgent)
        response.status = HttpServletResponse.SC_SEE_OTHER
        response.setHeader("Location", deviceType.location)
    }
}
