package odyseja.odysejapka.gad

import odyseja.odysejapka.cache.CacheKeys
import odyseja.odysejapka.cache.CacheService
import odyseja.odysejapka.gad.GadGlobalCache
import odyseja.odysejapka.cache.ZspCityCache
import org.springframework.stereotype.Service

@Service
class GadCommandService(private val cacheService: CacheService) {

    fun saveCommand(command: GenerateGadCommand, cityId: Int?) {
        cacheService.put(CacheKeys.GAD_GLOBAL, GadGlobalCache(
            templatesFolderId = command.templatesFolderId,
            problemPunctuationCells = command.problemPunctuationCells
        ))
        if (cityId != null) {
            cacheService.put(CacheKeys.zspCity(cityId), ZspCityCache(
                zspId = command.zspId,
                contestName = null
            ))
        }
    }

    fun getCommand(cityId: Int?): GenerateGadCommand {
        val global = cacheService.get(CacheKeys.GAD_GLOBAL, GadGlobalCache::class.java)
        val zsp = if (cityId != null) {
            cacheService.get(CacheKeys.zspCity(cityId), ZspCityCache::class.java)
        } else null

        return GenerateGadCommand(
            templatesFolderId = global?.templatesFolderId ?: "",
            destinationFolderId = "",
            zspId = zsp?.zspId ?: "",
            problemPunctuationCells = global?.problemPunctuationCells ?: emptyMap()
        )
    }
}
