package odyseja.odysejapka.form

import odyseja.odysejapka.timetable.PerformanceEntity
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamResultService(
    private val teamResultEntryRepository: TeamResultEntryRepository,
    private val formEntryRepository: FormEntryRepository,
    private val performanceRepository: PerformanceRepository,
) {

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        val performance = getPerformance(performanceId)
        if (request.results.isEmpty()) return

        val formEntryById = getFormEntriesById(request.results)
        val noElementByEntryId = extractNoElementByEntryId(request.results)
        val styleNameByEntryId = extractStyleNameByEntryId(request.results)
        val zeroBalsaByEntryId = extractZeroBalsaByEntryId(request.results)
        val commentByEntryId = extractCommentByEntryId(request.results)

        val resultEntries = processResults(
            performanceId,
            request.results,
            formEntryById,
            noElementByEntryId,
            styleNameByEntryId,
            zeroBalsaByEntryId,
            commentByEntryId,
            performance!!
        )
        val noElementUpdates = getNoElementUpdates(performanceId, noElementByEntryId)
        val styleNameUpdates = getStyleNameUpdates(performanceId, styleNameByEntryId)
        val zeroBalsaUpdates = getZeroBalsaUpdates(performanceId, zeroBalsaByEntryId)
        val commentUpdates = getCommentUpdates(performanceId, commentByEntryId)

        val toSave = combineResults(resultEntries, noElementUpdates, styleNameUpdates, zeroBalsaUpdates, commentUpdates)
        if (toSave.isNotEmpty()) {
            teamResultEntryRepository.saveAll(toSave)
        }
    }

    private fun combineResults(
        resultEntries: List<TeamResultEntryEntity>,
        noElementUpdates: List<TeamResultEntryEntity>,
        styleNameUpdates: List<TeamResultEntryEntity>,
        zeroBalsaUpdates: List<TeamResultEntryEntity>,
        commentUpdates: List<TeamResultEntryEntity>
    ): List<TeamResultEntryEntity> {
        val allEntries = resultEntries + noElementUpdates + styleNameUpdates + zeroBalsaUpdates + commentUpdates
        return allEntries.distinct()
    }

    private fun getPerformance(performanceId: Int) =
        performanceRepository.findById(performanceId)
            .orElseThrow { IllegalArgumentException("Performance $performanceId not found") }

    private fun getFormEntriesById(results: List<PerformanceResultsRequest.PerformanceResult>): Map<Long, FormEntryEntity> {
        val entryIds = results.map { it.entryId }.toSet()
        val formEntries = formEntryRepository.findAllById(entryIds)
        val formEntryById = formEntries.associateBy { it.id }
        val missing = entryIds - formEntryById.keys
        if (missing.isNotEmpty()) {
            throw IllegalArgumentException("Unknown form entry id(s): $missing")
        }
        return formEntryById
    }

    private fun extractNoElementByEntryId(results: List<PerformanceResultsRequest.PerformanceResult>) =
        results.groupBy { it.entryId }
            .mapValues { (_, results) -> results.firstOrNull()?.noElement ?: false }

    private fun extractStyleNameByEntryId(results: List<PerformanceResultsRequest.PerformanceResult>) =
        results.filter { it.judgeType == JudgeType.STYLE }
            .groupBy { it.entryId }
            .mapValues { (_, results) -> results.firstOrNull()?.styleName }

    private fun extractZeroBalsaByEntryId(results: List<PerformanceResultsRequest.PerformanceResult>) =
        results.groupBy { it.entryId }
            .mapValues { (_, results) -> results.firstOrNull()?.zeroBalsa ?: false }

    private fun extractCommentByEntryId(results: List<PerformanceResultsRequest.PerformanceResult>) =
        results.groupBy { it.entryId }
            .mapValues { (_, results) -> results.firstOrNull()?.comment }

    private fun processResults(
        performanceId: Int,
        results: List<PerformanceResultsRequest.PerformanceResult>,
        formEntryById: Map<Long, FormEntryEntity>,
        noElementByEntryId: Map<Long, Boolean>,
        styleNameByEntryId: Map<Long, String?>,
        zeroBalsaByEntryId: Map<Long, Boolean>,
        commentByEntryId: Map<Long, String?>,
        performance: PerformanceEntity
    ): List<TeamResultEntryEntity> {
        return results.mapNotNull { r ->
            val existing = findExistingEntry(performanceId, r)
            if (existing != null) {
                getUpdatedEntry(existing, r, noElementByEntryId, styleNameByEntryId, zeroBalsaByEntryId, commentByEntryId)
            } else {
                createNewEntry(r, formEntryById, noElementByEntryId, styleNameByEntryId, zeroBalsaByEntryId, commentByEntryId, performance)
            }
        }
    }

    private fun findExistingEntry(
        performanceId: Int,
        result: PerformanceResultsRequest.PerformanceResult
    ) = teamResultEntryRepository.findByPerformanceEntityIdAndFormEntryEntityIdAndJudgeTypeAndJudge(
        performanceId, result.entryId, result.judgeType, result.judge
    )

    private fun getUpdatedEntry(
        existing: TeamResultEntryEntity,
        result: PerformanceResultsRequest.PerformanceResult,
        noElementByEntryId: Map<Long, Boolean>,
        styleNameByEntryId: Map<Long, String?>,
        zeroBalsaByEntryId: Map<Long, Boolean>,
        commentByEntryId: Map<Long, String?>
    ): TeamResultEntryEntity? {
        val needsResultUpdate = existing.result != result.result
        val noElement = noElementByEntryId[result.entryId] ?: false
        val needsNoElementUpdate = existing.noElement != noElement
        val styleName = if (result.judgeType == JudgeType.STYLE) styleNameByEntryId[result.entryId] else null
        val needsStyleNameUpdate = result.judgeType == JudgeType.STYLE && existing.styleName != styleName
        val zeroBalsa = zeroBalsaByEntryId[result.entryId] ?: false
        val needsZeroBalsaUpdate = existing.zeroBalsa != zeroBalsa
        val comment = commentByEntryId[result.entryId]
        val needsCommentUpdate = existing.comment != comment

        if (!needsResultUpdate && !needsNoElementUpdate && !needsStyleNameUpdate && !needsZeroBalsaUpdate && !needsCommentUpdate) {
            return null
        }

        if (needsResultUpdate) existing.result = result.result
        if (needsNoElementUpdate) existing.noElement = noElement
        if (needsStyleNameUpdate) existing.styleName = styleName
        if (needsZeroBalsaUpdate) existing.zeroBalsa = zeroBalsa
        if (needsCommentUpdate) existing.comment = comment
        return existing
    }

    private fun createNewEntry(
        result: PerformanceResultsRequest.PerformanceResult,
        formEntryById: Map<Long, FormEntryEntity>,
        noElementByEntryId: Map<Long, Boolean>,
        styleNameByEntryId: Map<Long, String?>,
        zeroBalsaByEntryId: Map<Long, Boolean>,
        commentByEntryId: Map<Long, String?>,
        performance: PerformanceEntity
    ): TeamResultEntryEntity {
        return TeamResultEntryEntity().apply {
            performanceEntity = performance
            formEntryEntity = formEntryById.getValue(result.entryId)
            judgeType = result.judgeType
            judge = result.judge
            this.result = result.result
            noElement = noElementByEntryId[result.entryId] ?: false
            zeroBalsa = zeroBalsaByEntryId[result.entryId] ?: false
            comment = commentByEntryId[result.entryId]
            if (result.judgeType == JudgeType.STYLE) {
                styleName = styleNameByEntryId[result.entryId]
            }
        }
    }

    private fun getNoElementUpdates(
        performanceId: Int,
        noElementByEntryId: Map<Long, Boolean>
    ): List<TeamResultEntryEntity> {
        return noElementByEntryId.flatMap { (entryId, noElement) ->
            getNoElementUpdatesForEntry(performanceId, entryId, noElement)
        }
    }

    private fun getNoElementUpdatesForEntry(
        performanceId: Int,
        entryId: Long,
        noElement: Boolean
    ): List<TeamResultEntryEntity> {
        val allEntries = teamResultEntryRepository
            .findByPerformanceEntityIdAndFormEntryEntityId(performanceId, entryId)
        return allEntries.filter { it.noElement != noElement }
            .onEach { it.noElement = noElement }
    }

    private fun getStyleNameUpdates(
        performanceId: Int,
        styleNameByEntryId: Map<Long, String?>
    ): List<TeamResultEntryEntity> {
        return styleNameByEntryId.flatMap { (entryId, styleName) ->
            getStyleNameUpdatesForEntry(performanceId, entryId, styleName)
        }
    }

    private fun getStyleNameUpdatesForEntry(
        performanceId: Int,
        entryId: Long,
        styleName: String?
    ): List<TeamResultEntryEntity> {
        val allStyleEntries = teamResultEntryRepository
            .findByPerformanceEntityIdAndFormEntryEntityId(performanceId, entryId)
            .filter { it.judgeType == JudgeType.STYLE }
        return allStyleEntries.filter { it.styleName != styleName }
            .onEach { it.styleName = styleName }
    }

    private fun getZeroBalsaUpdates(
        performanceId: Int,
        zeroBalsaByEntryId: Map<Long, Boolean>
    ): List<TeamResultEntryEntity> {
        return zeroBalsaByEntryId.flatMap { (entryId, zeroBalsa) ->
            getZeroBalsaUpdatesForEntry(performanceId, entryId, zeroBalsa)
        }
    }

    private fun getZeroBalsaUpdatesForEntry(
        performanceId: Int,
        entryId: Long,
        zeroBalsa: Boolean
    ): List<TeamResultEntryEntity> {
        val allEntries = teamResultEntryRepository
            .findByPerformanceEntityIdAndFormEntryEntityId(performanceId, entryId)
        return allEntries.filter { it.zeroBalsa != zeroBalsa }
            .onEach { it.zeroBalsa = zeroBalsa }
    }

    private fun getCommentUpdates(
        performanceId: Int,
        commentByEntryId: Map<Long, String?>
    ): List<TeamResultEntryEntity> {
        return commentByEntryId.flatMap { (entryId, comment) ->
            getCommentUpdatesForEntry(performanceId, entryId, comment)
        }
    }

    private fun getCommentUpdatesForEntry(
        performanceId: Int,
        entryId: Long,
        comment: String?
    ): List<TeamResultEntryEntity> {
        val allEntries = teamResultEntryRepository
            .findByPerformanceEntityIdAndFormEntryEntityId(performanceId, entryId)
        return allEntries.filter { it.comment != comment }
            .onEach { it.comment = comment }
    }
}

