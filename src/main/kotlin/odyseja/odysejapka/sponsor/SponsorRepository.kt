package odyseja.odysejapka.sponsor

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SponsorRepository : CrudRepository<SponsorEntity, Int> {

    fun findAllByCity_Id(cityId: Int): List<SponsorEntity>

    @Query("SELECT MAX(s.columnOrder) FROM sponsor s WHERE s.rowOrder = :rowOrder AND s.city.id = :cityId")
    fun findMaxColumnOrderByRowOrderAndCity_Id(rowOrder: Int, cityId: Int): Int?
}