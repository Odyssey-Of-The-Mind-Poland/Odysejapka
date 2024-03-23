package odyseja.odysejapka.qr

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/lappka/download")
internal class MobileAppRedirectController {
    @GetMapping
    fun detectDevice(request: HttpServletRequest): String {
        val userAgent = request.getHeader("User-Agent")
        var deviceType = getDeviceType(userAgent)

        return "Detected Device Type: $deviceType"
    }

    private fun getDeviceType(userAgent: String): String {

        if (userAgent.lowercase(Locale.getDefault()).contains("android")) {
            return "Android"
        }

        if (userAgent.lowercase(Locale.getDefault())
                .contains("iphone") || userAgent.lowercase(Locale.getDefault()).contains("ipad")
        ) {
            return "iOS"
        }

        return userAgent
    }
}
