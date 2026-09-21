package odyseja.odysejapka.form.problem.form

import odyseja.odysejapka.form.LongTermFormEntry
import odyseja.odysejapka.form.PenaltyFormEntry
import odyseja.odysejapka.form.StyleFormEntry
import odyseja.odysejapka.form.judge.team.JudgeTeam

/**
 * Dane do arkusza z kategoriami punktacji, ale jeszcze nieprzydzielone do konkretnego problemu.
 */

data class FormData(
    val dtEntries: List<LongTermFormEntry>,
    val styleEntries: List<StyleFormEntry>,
    val penaltyEntries: List<PenaltyFormEntry>,
    val smallJudgesTeam: List<Int>? = null,
    val bigJudgesTeam: List<Int>? = null,
    val judgeTeams: List<JudgeTeam?> = emptyList()
)
