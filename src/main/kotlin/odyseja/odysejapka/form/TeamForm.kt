package odyseja.odysejapka.form

data class TeamForm(val performanceId: Int, val entries: List<FormEntryRow>) {
    data class FormEntryRow(val entryId: Long, val judgeResults: Map<Int, Long>)
}
