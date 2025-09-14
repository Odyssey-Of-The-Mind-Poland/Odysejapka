package odyseja.odysejapka.qr

import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*
import jakarta.transaction.Transactional

@Service
class QrService(
    private val scanRepository: ScanRepository
) {

    @Transactional
    fun getDeviceType(userAgent: String): DeviceType {
        val deviceType = this.getType(userAgent)
        scanRepository.save(ScanEntity(0, deviceType.name, Timestamp(System.currentTimeMillis())))
        return deviceType
    }

    private fun getType(userAgent: String): DeviceType {

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