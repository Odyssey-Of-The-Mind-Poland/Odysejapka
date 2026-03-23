package odyseja.odysejapka.stage

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository


interface StageRepository : CrudRepository<StageEntity, Int> {
    fun findFirstByNumberAndCityEntity(number: Int, cityEntity: CityEntity): StageEntity?
    fun findByCityEntity(id: CityEntity): MutableIterable<StageEntity?>
    fun deleteByCityEntity(cityEntity: CityEntity)
}