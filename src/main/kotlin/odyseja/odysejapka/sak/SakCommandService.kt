package odyseja.odysejapka.sak

import odyseja.odysejapka.cache.CacheKeys
import odyseja.odysejapka.cache.CacheService
import odyseja.odysejapka.cache.ZspCityCache
import odyseja.odysejapka.gad.GadGlobalCache
import org.springframework.stereotype.Service

@Service
class SakCommandService(private val cacheService: CacheService) {

    fun saveCommand(command: GenerateSakCommand, cityId: Int?) {
        if (cityId != null) {
            cacheService.put(CacheKeys.zspCity(cityId), ZspCityCache(
                zspId = command.zspId,
                contestName = null
            ))
        }
    }

    fun getCommand(cityId: Int?): GenerateSakCommand {
        val gadGlobal = cacheService.get(CacheKeys.GAD_GLOBAL, GadGlobalCache::class.java)
        val zsp = if (cityId != null) {
            cacheService.get(CacheKeys.zspCity(cityId), ZspCityCache::class.java)
        } else null

        return GenerateSakCommand(
            templatesFolderId = gadGlobal?.templatesFolderId ?: "",
            zspId = zsp?.zspId ?: ""
        )
    }
}
