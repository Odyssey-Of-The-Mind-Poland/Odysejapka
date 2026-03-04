package odyseja.odysejapka.cache

object CacheKeys {
    const val GAD_GLOBAL = "gad:global"
    fun zspCity(cityId: Int) = "zsp:city:$cityId"
    fun sakCity(cityId: Int) = "sak:city:$cityId"
}
