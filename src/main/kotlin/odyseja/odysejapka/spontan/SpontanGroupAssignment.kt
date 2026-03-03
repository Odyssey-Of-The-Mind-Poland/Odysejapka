package odyseja.odysejapka.spontan

data class SpontanGroupAssignment(
    val id: Long? = null,
    val cityId: Int,
    val groupId: GroupId,
    val spontanDefinitionId: Long? = null,
    val spontanDefinitionName: String? = null,
    val spontanType: SpontanType? = null,
    val judgeCount: Int = 3
)

data class AssignSpontanRequest(
    val spontanDefinitionId: Long?
)

data class SetJudgeCountRequest(
    val judgeCount: Int
)
