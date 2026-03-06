package odyseja.odysejapka.harmonogram

data class HarmonogramTeam(
    val id: Int,
    val teamName: String,
    val problem: Int,
    val age: Int,
    val league: String?
)

data class HarmonogramStage(
    val id: Int,
    val number: Int,
    val name: String,
    val city: Int
)

data class TimeSlotDto(
    val id: Int,
    val stageId: Int,
    val startTime: String,
    val endTime: String,
    val teamId: Int?,
    val teamName: String?
)

data class SpontanRoomDto(
    val id: Int,
    val name: String,
    val city: Int
)

data class SpontanSlotDto(
    val id: Int,
    val roomId: Int,
    val startTime: String,
    val endTime: String,
    val teamId: Int?,
    val teamName: String?
)

data class CreateTimeSlotRequest(
    val stageId: Int,
    val startTime: String,
    val endTime: String
)

data class CreateSpontanRoomRequest(
    val name: String
)

data class CreateSpontanSlotRequest(
    val roomId: Int,
    val startTime: String,
    val endTime: String
)

data class GenerateTimeSlotsRequest(
    val stageId: Int,
    val startTime: String,
    val slotDurationMinutes: Int,
    val count: Int
)

data class GenerateSpontanSlotsRequest(
    val roomId: Int,
    val startTime: String,
    val slotDurationMinutes: Int,
    val count: Int
)
