package odyseja.odysejapka.harmonogram

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/harmonogram")
class HarmonogramController(
    private val harmonogramService: HarmonogramService
) {

    @GetMapping("/{cityId}/teams")
    fun getTeams(@PathVariable cityId: Int): List<HarmonogramTeam> {
        return harmonogramService.getTeams(cityId)
    }

    @GetMapping("/{cityId}/stages")
    fun getStages(@PathVariable cityId: Int): List<HarmonogramStage> {
        return harmonogramService.getStages(cityId)
    }

    @GetMapping("/{cityId}/time-slots")
    fun getTimeSlots(@PathVariable cityId: Int): List<TimeSlotDto> {
        return harmonogramService.getTimeSlots(cityId)
    }

    @GetMapping("/{cityId}/spontan-rooms")
    fun getSpontanRooms(@PathVariable cityId: Int): List<SpontanRoomDto> {
        return harmonogramService.getSpontanRooms(cityId)
    }

    @GetMapping("/{cityId}/spontan-slots")
    fun getSpontanSlots(@PathVariable cityId: Int): List<SpontanSlotDto> {
        return harmonogramService.getSpontanSlots(cityId)
    }

    // Time slot management

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/time-slots")
    fun createTimeSlot(
        @PathVariable cityId: Int,
        @RequestBody request: CreateTimeSlotRequest
    ): TimeSlotDto {
        return harmonogramService.createTimeSlot(cityId, request)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{cityId}/time-slots/{slotId}")
    fun deleteTimeSlot(@PathVariable cityId: Int, @PathVariable slotId: Int) {
        harmonogramService.deleteTimeSlot(slotId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/time-slots/{slotId}/assign/{teamId}")
    fun assignTeamToTimeSlot(
        @PathVariable cityId: Int,
        @PathVariable slotId: Int,
        @PathVariable teamId: Int
    ) {
        harmonogramService.assignTeamToTimeSlot(slotId, teamId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/time-slots/{slotId}/unassign")
    fun unassignTeamFromTimeSlot(@PathVariable cityId: Int, @PathVariable slotId: Int) {
        harmonogramService.unassignTeamFromTimeSlot(slotId)
    }

    // Spontan room management

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/spontan-rooms")
    fun createSpontanRoom(
        @PathVariable cityId: Int,
        @RequestBody request: CreateSpontanRoomRequest
    ): SpontanRoomDto {
        return harmonogramService.createSpontanRoom(cityId, request)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{cityId}/spontan-rooms/{roomId}")
    fun deleteSpontanRoom(@PathVariable cityId: Int, @PathVariable roomId: Int) {
        harmonogramService.deleteSpontanRoom(roomId)
    }

    // Spontan slot management

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/spontan-slots")
    fun createSpontanSlot(
        @PathVariable cityId: Int,
        @RequestBody request: CreateSpontanSlotRequest
    ): SpontanSlotDto {
        return harmonogramService.createSpontanSlot(cityId, request)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{cityId}/spontan-slots/{slotId}")
    fun deleteSpontanSlot(@PathVariable cityId: Int, @PathVariable slotId: Int) {
        harmonogramService.deleteSpontanSlot(slotId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/spontan-slots/{slotId}/assign/{teamId}")
    fun assignTeamToSpontanSlot(
        @PathVariable cityId: Int,
        @PathVariable slotId: Int,
        @PathVariable teamId: Int
    ) {
        harmonogramService.assignTeamToSpontanSlot(slotId, teamId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/spontan-slots/{slotId}/unassign")
    fun unassignTeamFromSpontanSlot(@PathVariable cityId: Int, @PathVariable slotId: Int) {
        harmonogramService.unassignTeamFromSpontanSlot(slotId)
    }

    // Batch generation

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/time-slots/generate")
    fun generateTimeSlots(
        @PathVariable cityId: Int,
        @RequestBody request: GenerateTimeSlotsRequest
    ): List<TimeSlotDto> {
        return harmonogramService.generateTimeSlots(cityId, request)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/{cityId}/spontan-slots/generate")
    fun generateSpontanSlots(
        @PathVariable cityId: Int,
        @RequestBody request: GenerateSpontanSlotsRequest
    ): List<SpontanSlotDto> {
        return harmonogramService.generateSpontanSlots(cityId, request)
    }
}
