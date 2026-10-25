package odyseja.odysejapka.form

/**
 * Dane do arkusza z kategoriami punktacji, ale jeszcze nieprzydzielone do konkretnego problemu.
 */

data class FormData(
    val dtEntries: List<LongTermFormEntry>,
    val styleEntries: List<StyleFormEntry>,
    val penaltyEntries: List<PenaltyFormEntry>,
    val judgeTeams: List<JudgeTeam>? = null
)
