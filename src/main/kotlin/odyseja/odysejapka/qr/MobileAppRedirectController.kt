package odyseja.odysejapka.qr

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/lappka/download")
internal class MobileAppRedirectController {
    @GetMapping
    fun detectDevice(request: HttpServletRequest, response: HttpServletResponse) {
        val userAgent = request.getHeader("User-Agent")
        var deviceType = getDeviceType(userAgent)
        response.status = HttpServletResponse.SC_SEE_OTHER
        response.setHeader("Location", deviceType.location)
    }

    private fun getDeviceType(userAgent: String): DeviceType {

        if (userAgent.lowercase(Locale.getDefault()).contains("android")) {
            return DeviceType.ANDROID
        }

        if (userAgent.lowercase(Locale.getDefault())
                .contains("iphone") || userAgent.lowercase(Locale.getDefault()).contains("ipad")
        ) {
            return DeviceType.IOS
        }

        return DeviceType.OTHER
    }
}
