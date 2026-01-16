package odyseja.odysejapka.form

/**
 * Helper to create a map of entry_id -> entry from a ProblemForm
 */
object FormEntryMapHelper {

    /**
     * Creates a map of entry_id -> entry for all entries in the form
     */
    fun createEntryMap(form: ProblemForm): Map<Long, Any> {
        val map = mutableMapOf<Long, Any>()
        
        form.dtEntries.forEach { entry ->
            collectEntries(entry, map)
        }
        
        form.styleEntries.forEach { entry ->
            collectEntries(entry, map)
        }
        
        form.penaltyEntries.forEach { entry ->
            collectEntries(entry, map)
        }
        
        return map
    }

    private fun <T> collectEntries(
        entry: T,
        map: MutableMap<Long, Any>
    ) where T : Any {
        when (entry) {
            is LongTermFormEntry -> {
                entry.id?.let { map[it] = entry }
                entry.entries.forEach { child ->
                    collectEntries(child, map)
                }
            }
            is StyleFormEntry -> {
                entry.id?.let { map[it] = entry }
                entry.entries.forEach { child ->
                    collectEntries(child, map)
                }
            }
            is PenaltyFormEntry -> {
                entry.id?.let { map[it] = entry }
                entry.entries.forEach { child ->
                    collectEntries(child, map)
                }
            }
        }
    }

    /**
     * Assigns IDs to entries that don't have them, preserving existing IDs
     */
    fun assignIds(form: ProblemForm, existingForm: ProblemForm?): ProblemForm {
        val existingIds = existingForm?.let { createEntryMap(it).keys } ?: emptySet()
        val nextIdHolder = mutableListOf((existingIds.maxOrNull() ?: 0L) + 1L)

        fun getNextId(): Long {
            return nextIdHolder[0]++
        }

        val dtEntries = form.dtEntries.map { assignIdToLongTerm(it, existingIds, ::getNextId) }
        val styleEntries = form.styleEntries.map { assignIdToStyle(it, existingIds, ::getNextId) }
        val penaltyEntries = form.penaltyEntries.map { assignIdToPenalty(it, existingIds, ::getNextId) }

        return form.copy(
            dtEntries = dtEntries,
            styleEntries = styleEntries,
            penaltyEntries = penaltyEntries
        )
    }

    private fun assignIdToLongTerm(
        entry: LongTermFormEntry,
        existingIds: Set<Long>,
        nextIdProvider: () -> Long
    ): LongTermFormEntry {
        val id = entry.id ?: generateNextId(existingIds, nextIdProvider)
        val children = entry.entries.map { assignIdToLongTerm(it, existingIds, nextIdProvider) }
        return entry.copy(id = id, entries = children)
    }

    private fun assignIdToStyle(
        entry: StyleFormEntry,
        existingIds: Set<Long>,
        nextIdProvider: () -> Long
    ): StyleFormEntry {
        val id = entry.id ?: generateNextId(existingIds, nextIdProvider)
        val children = entry.entries.map { assignIdToStyle(it, existingIds, nextIdProvider) }
        return entry.copy(id = id, entries = children)
    }

    private fun assignIdToPenalty(
        entry: PenaltyFormEntry,
        existingIds: Set<Long>,
        nextIdProvider: () -> Long
    ): PenaltyFormEntry {
        val id = entry.id ?: generateNextId(existingIds, nextIdProvider)
        val children = entry.entries.map { assignIdToPenalty(it, existingIds, nextIdProvider) }
        return entry.copy(id = id, entries = children)
    }

    private fun generateNextId(existingIds: Set<Long>, nextIdProvider: () -> Long): Long {
        var id = nextIdProvider()
        while (id in existingIds) {
            id++
        }
        return id
    }
}

