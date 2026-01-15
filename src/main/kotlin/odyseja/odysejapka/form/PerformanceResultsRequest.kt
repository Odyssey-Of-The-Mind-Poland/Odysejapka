package odyseja.odysejapka.form

data class PerformanceResultsRequest(val results: List<PerformanceResult>) {
    data class PerformanceResult(val entryId: Long, val judgeType: JudgeType, val judge: Int, val result: Long, val noElement: Boolean? = null, val styleName: String? = null, val zeroBalsa: Boolean? = null, val comment: String? = null)
}
