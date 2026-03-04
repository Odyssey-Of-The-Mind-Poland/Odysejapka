package odyseja.odysejapka.rak

import odyseja.odysejapka.cache.CacheKeys
import odyseja.odysejapka.cache.CacheService
import odyseja.odysejapka.cache.ZspCityCache
import org.springframework.stereotype.Service

@Service
class RakCommandService(private val cacheService: CacheService) {

    fun saveCommand(zspId: String, contestName: String?, cityId: Int?) {
        if (cityId != null) {
            cacheService.put(CacheKeys.zspCity(cityId), ZspCityCache(
                zspId = zspId,
                contestName = contestName
            ))
        }
    }

    fun getLastCommand(cityId: Int?): ZspIdRequest {
        val zsp = if (cityId != null) {
            cacheService.get(CacheKeys.zspCity(cityId), ZspCityCache::class.java)
        } else null

        return ZspIdRequest(
            zspId = zsp?.zspId ?: "",
            contestName = zsp?.contestName
        )
    }
}
