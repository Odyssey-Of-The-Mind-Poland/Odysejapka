package odyseja.odysejapka.sponsor

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SponsorRepository : CrudRepository<SponsorEntity, Int> {

    fun findFirstById(id: Int): SponsorEntity?

    fun findAllByCityId(cityId: Int): List<SponsorEntity>

    fun deleteByCityId(cityId: Int)

    @Query("SELECT MAX(s.columnOrder) FROM sponsor s WHERE s.rowOrder = :rowOrder AND s.city.id = :cityId")
    fun findMaxColumnOrderByRowOrderAndCityId(rowOrder: Int, cityId: Int): Int?
}