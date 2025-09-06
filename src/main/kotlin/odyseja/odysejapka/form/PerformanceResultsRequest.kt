package odyseja.odysejapka.form

data class PerformanceResultsRequest(val results: List<PerformanceResult>) {
    data class PerformanceResult(val entryId: Long, val result: Long, val judge: Int)
}
