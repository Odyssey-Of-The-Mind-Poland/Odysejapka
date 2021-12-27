package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.domain.StageEntity
import org.springframework.data.repository.CrudRepository
import java.util.*


interface StageRepository : CrudRepository<StageEntity, Int> {
    fun findFirstById(id: Int?): StageEntity?
    fun findFirstByNumberAndCityEntity(number: Int?, cityEntity: CityEntity?): StageEntity?
    fun findAllByCityEntity(id: Optional<CityEntity?>): MutableIterable<StageEntity?>
}