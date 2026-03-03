package odyseja.odysejapka.spontan

data class VerbalJudgeEntry(
    val judge: Int,
    val creative: Double = 0.0,
    val common: Double = 0.0
)

data class ManualJudgeEntry(
    val judge: Int,
    val creativity: Double = 0.0,
    val teamwork: Double = 0.0
)

data class ManualScoreEntry(
    val field: String,
    val value: Double
)

data class SpontanResults(
    val verbalEntries: List<VerbalJudgeEntry> = emptyList(),
    val manualJudgeEntries: List<ManualJudgeEntry> = emptyList(),
    val manualEntries: List<ManualScoreEntry> = emptyList()
)

data class SpontanResultsRequest(
    val verbalEntries: List<VerbalJudgeEntry> = emptyList(),
    val manualJudgeEntries: List<ManualJudgeEntry> = emptyList(),
    val manualEntries: List<ManualScoreEntry> = emptyList()
)

data class SpontanTeamResult(
    val performanceId: Int,
    val team: String,
    val spontanHour: String,
    val verbalEntries: List<VerbalJudgeEntry>,
    val manualJudgeEntries: List<ManualJudgeEntry>,
    val manualEntries: List<ManualScoreEntry>,
    val rawSpontan: Double?
)

data class SpontanGroupTeams(
    val spontanDefinition: SpontanDefinition,
    val judgeCount: Int,
    val teams: List<SpontanTeamResult>
)
