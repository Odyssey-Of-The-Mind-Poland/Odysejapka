package odyseja.odysejapka.rak

import odyseja.odysejapka.rak.ZspIdRequest
import org.springframework.stereotype.Service

@Service
class RakCommandService(
    private val repository: RakCommandRepository
) {

    fun saveCommand(zspId: String, cityId: Int?) {
        repository.save(
            RakCommandEntity(
                cityId = cityId,
                zspId = zspId
            )
        )
    }

    fun getLastCommand(cityId: Int?): ZspIdRequest {
        val entity = if (cityId != null) {
            repository.findFirstByCityIdOrderByIdDesc(cityId)
        } else {
            repository.findFirstByOrderByIdDesc()
        }

        return if (entity == null) {
            ZspIdRequest(zspId = "")
        } else {
            ZspIdRequest(zspId = entity.zspId)
        }
    }
}


