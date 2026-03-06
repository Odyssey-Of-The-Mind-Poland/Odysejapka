package odyseja.odysejapka.sak

import odyseja.odysejapka.cache.CacheKeys
import odyseja.odysejapka.cache.CacheService
import odyseja.odysejapka.cache.ZspCityCache
import org.springframework.stereotype.Service

@Service
class SakCommandService(private val cacheService: CacheService) {

    fun saveCommand(command: GenerateSakCommand, cityId: Int?) {
        if (cityId != null) {
            cacheService.put(CacheKeys.sakCity(cityId), SakCityCache(
                templatesFolderId = command.templatesFolderId
            ))
            cacheService.put(CacheKeys.zspCity(cityId), ZspCityCache(
                zspId = command.zspId,
                contestName = null
            ))
        }
    }

    fun getCommand(cityId: Int?): GenerateSakCommand {
        val sakCache = if (cityId != null) {
            cacheService.get(CacheKeys.sakCity(cityId), SakCityCache::class.java)
        } else null
        val zsp = if (cityId != null) {
            cacheService.get(CacheKeys.zspCity(cityId), ZspCityCache::class.java)
        } else null

        return GenerateSakCommand(
            templatesFolderId = sakCache?.templatesFolderId ?: "",
            zspId = zsp?.zspId ?: ""
        )
    }
}
