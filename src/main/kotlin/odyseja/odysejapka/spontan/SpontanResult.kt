package odyseja.odysejapka.spontan

data class SpontanResultsRequest(
    val entries: List<SpontanResultEntry>
)

data class SpontanResultEntry(
    val judge: Int,
    val field: String,
    val value: Double
)

data class SpontanTeamResult(
    val performanceId: Int,
    val team: String,
    val spontanHour: String,
    val entries: List<SpontanResultEntry>,
    val rawSpontan: Double?
)

data class SpontanGroupTeams(
    val spontanDefinition: SpontanDefinition,
    val judgeCount: Int,
    val teams: List<SpontanTeamResult>
)
