package odyseja.odysejapka.harmonogram

import org.springframework.data.repository.CrudRepository

interface HarmonogramTimeSlotRepository : CrudRepository<HarmonogramTimeSlotEntity, Int> {
    fun findAllByStageEntity_CityEntity_Id(cityId: Int): List<HarmonogramTimeSlotEntity>
}

interface SpontanRoomRepository : CrudRepository<SpontanRoomEntity, Int> {
    fun findAllByCityEntity_Id(cityId: Int): List<SpontanRoomEntity>
}

interface SpontanSlotRepository : CrudRepository<SpontanSlotEntity, Int> {
    fun findAllBySpontanRoomEntity_CityEntity_Id(cityId: Int): List<SpontanSlotEntity>
}
