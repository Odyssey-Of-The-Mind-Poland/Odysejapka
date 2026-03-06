package odyseja.odysejapka.harmonogram

import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.stage.StageRepository
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class HarmonogramService(
    private val performanceRepository: PerformanceRepository,
    private val cityRepository: CityRepository,
    private val stageRepository: StageRepository,
    private val timeSlotRepository: HarmonogramTimeSlotRepository,
    private val spontanRoomRepository: SpontanRoomRepository,
    private val spontanSlotRepository: SpontanSlotRepository
) {

    fun getTeams(cityId: Int): List<HarmonogramTeam> {
        return performanceRepository.findAllByCityEntity_Id(cityId).map { perf ->
            HarmonogramTeam(
                id = perf.id,
                teamName = perf.team,
                problem = perf.problemEntity.id,
                age = perf.ageEntity.id,
                league = perf.league
            )
        }
    }

    fun getStages(cityId: Int): List<HarmonogramStage> {
        @Suppress("UNCHECKED_CAST")
        val cityOpt = java.util.Optional.of(cityRepository.findFirstById(cityId)) as java.util.Optional<CityEntity?>
        return stageRepository.findAllByCityEntity(cityOpt)
            .filterNotNull()
            .map { stage ->
                HarmonogramStage(
                    id = stage.id,
                    number = stage.number,
                    name = stage.name,
                    city = stage.cityEntity.id
                )
            }
    }

    fun getTimeSlots(cityId: Int): List<TimeSlotDto> {
        return timeSlotRepository.findAllByStageEntity_CityEntity_Id(cityId).map { slot ->
            TimeSlotDto(
                id = slot.id,
                stageId = slot.stageEntity.id,
                startTime = slot.startTime,
                endTime = slot.endTime,
                teamId = slot.performanceEntity?.id,
                teamName = slot.performanceEntity?.team
            )
        }
    }

    fun getSpontanRooms(cityId: Int): List<SpontanRoomDto> {
        return spontanRoomRepository.findAllByCityEntity_Id(cityId).map { room ->
            SpontanRoomDto(
                id = room.id,
                name = room.name,
                city = room.cityEntity.id
            )
        }
    }

    fun getSpontanSlots(cityId: Int): List<SpontanSlotDto> {
        return spontanSlotRepository.findAllBySpontanRoomEntity_CityEntity_Id(cityId).map { slot ->
            SpontanSlotDto(
                id = slot.id,
                roomId = slot.spontanRoomEntity.id,
                startTime = slot.startTime,
                endTime = slot.endTime,
                teamId = slot.performanceEntity?.id,
                teamName = slot.performanceEntity?.team
            )
        }
    }

    @Transactional
    fun assignTeamToTimeSlot(slotId: Int, teamId: Int) {
        val slot = timeSlotRepository.findById(slotId)
            .orElseThrow { IllegalArgumentException("Slot $slotId not found") }
        val performance = performanceRepository.findById(teamId)
            .orElseThrow { IllegalArgumentException("Performance $teamId not found") }
        slot.performanceEntity = performance
        timeSlotRepository.save(slot)
    }

    @Transactional
    fun unassignTeamFromTimeSlot(slotId: Int) {
        val slot = timeSlotRepository.findById(slotId)
            .orElseThrow { IllegalArgumentException("Slot $slotId not found") }
        slot.performanceEntity = null
        timeSlotRepository.save(slot)
    }

    @Transactional
    fun assignTeamToSpontanSlot(slotId: Int, teamId: Int) {
        val slot = spontanSlotRepository.findById(slotId)
            .orElseThrow { IllegalArgumentException("Slot $slotId not found") }
        val performance = performanceRepository.findById(teamId)
            .orElseThrow { IllegalArgumentException("Performance $teamId not found") }
        slot.performanceEntity = performance
        spontanSlotRepository.save(slot)
    }

    @Transactional
    fun unassignTeamFromSpontanSlot(slotId: Int) {
        val slot = spontanSlotRepository.findById(slotId)
            .orElseThrow { IllegalArgumentException("Slot $slotId not found") }
        slot.performanceEntity = null
        spontanSlotRepository.save(slot)
    }

    @Transactional
    fun createTimeSlot(cityId: Int, request: CreateTimeSlotRequest): TimeSlotDto {
        val stage = stageRepository.findFirstById(request.stageId)
            ?: throw IllegalArgumentException("Stage ${request.stageId} not found")
        val entity = HarmonogramTimeSlotEntity(
            stageEntity = stage,
            startTime = request.startTime,
            endTime = request.endTime
        )
        val saved = timeSlotRepository.save(entity)
        return TimeSlotDto(
            id = saved.id,
            stageId = saved.stageEntity.id,
            startTime = saved.startTime,
            endTime = saved.endTime,
            teamId = null,
            teamName = null
        )
    }

    @Transactional
    fun deleteTimeSlot(slotId: Int) {
        timeSlotRepository.deleteById(slotId)
    }

    @Transactional
    fun createSpontanRoom(cityId: Int, request: CreateSpontanRoomRequest): SpontanRoomDto {
        val city = cityRepository.findFirstById(cityId)
        val entity = SpontanRoomEntity(name = request.name, cityEntity = city)
        val saved = spontanRoomRepository.save(entity)
        return SpontanRoomDto(id = saved.id, name = saved.name, city = cityId)
    }

    @Transactional
    fun deleteSpontanRoom(roomId: Int) {
        spontanRoomRepository.deleteById(roomId)
    }

    @Transactional
    fun createSpontanSlot(cityId: Int, request: CreateSpontanSlotRequest): SpontanSlotDto {
        val room = spontanRoomRepository.findById(request.roomId)
            .orElseThrow { IllegalArgumentException("Room ${request.roomId} not found") }
        val entity = SpontanSlotEntity(
            spontanRoomEntity = room,
            startTime = request.startTime,
            endTime = request.endTime
        )
        val saved = spontanSlotRepository.save(entity)
        return SpontanSlotDto(
            id = saved.id,
            roomId = saved.spontanRoomEntity.id,
            startTime = saved.startTime,
            endTime = saved.endTime,
            teamId = null,
            teamName = null
        )
    }

    @Transactional
    fun deleteSpontanSlot(slotId: Int) {
        spontanSlotRepository.deleteById(slotId)
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    @Transactional
    fun generateTimeSlots(cityId: Int, request: GenerateTimeSlotsRequest): List<TimeSlotDto> {
        val stage = stageRepository.findFirstById(request.stageId)
            ?: throw IllegalArgumentException("Stage ${request.stageId} not found")
        var current = LocalTime.parse(request.startTime, timeFormatter)
        val slots = mutableListOf<HarmonogramTimeSlotEntity>()
        repeat(request.count) {
            val end = current.plusMinutes(request.slotDurationMinutes.toLong())
            slots.add(HarmonogramTimeSlotEntity(
                stageEntity = stage,
                startTime = current.format(timeFormatter),
                endTime = end.format(timeFormatter)
            ))
            current = end
        }
        return timeSlotRepository.saveAll(slots).map { slot ->
            TimeSlotDto(
                id = slot.id,
                stageId = slot.stageEntity.id,
                startTime = slot.startTime,
                endTime = slot.endTime,
                teamId = null,
                teamName = null
            )
        }
    }

    @Transactional
    fun generateSpontanSlots(cityId: Int, request: GenerateSpontanSlotsRequest): List<SpontanSlotDto> {
        val room = spontanRoomRepository.findById(request.roomId)
            .orElseThrow { IllegalArgumentException("Room ${request.roomId} not found") }
        var current = LocalTime.parse(request.startTime, timeFormatter)
        val slots = mutableListOf<SpontanSlotEntity>()
        repeat(request.count) {
            val end = current.plusMinutes(request.slotDurationMinutes.toLong())
            slots.add(SpontanSlotEntity(
                spontanRoomEntity = room,
                startTime = current.format(timeFormatter),
                endTime = end.format(timeFormatter)
            ))
            current = end
        }
        return spontanSlotRepository.saveAll(slots).map { slot ->
            SpontanSlotDto(
                id = slot.id,
                roomId = slot.spontanRoomEntity.id,
                startTime = slot.startTime,
                endTime = slot.endTime,
                teamId = null,
                teamName = null
            )
        }
    }
}
