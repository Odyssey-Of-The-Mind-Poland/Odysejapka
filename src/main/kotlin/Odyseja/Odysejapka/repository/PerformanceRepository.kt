package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.Performance
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface PerformanceRepository : CrudRepository<Performance?, Int?> {
    @Query("SELECT t FROM Performance t WHERE t.team LIKE %:team% AND t.city LIKE %:city% AND t.problem LIKE %:problem% AND t.age LIKE %:age% AND t.stage LIKE %:stage%")
    fun findLike(@Param("team") team: String?, @Param("city") city: String?,
                 @Param("problem") problem: String?, @Param("age") age: String?, @Param("stage") stage: String?): Iterable<Performance?>?
    fun deleteByCity(city: String?)
}