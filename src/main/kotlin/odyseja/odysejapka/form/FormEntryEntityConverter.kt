package odyseja.odysejapka.form

object FormEntryEntityConverter {

    fun flattenLongTermToEntities(
        problem: Int,
        entries: List<LongTermFormEntry>,
        category: FormEntryEntity.FormCategory,
        existingById: Map<Long, FormEntryEntity> = emptyMap()
    ): List<FormEntryEntity> {
        return flattenToEntities(
            entries,
            toEntity = { entry, parent, _ ->
                entry.toEntity(problem, category, parent, entry.id?.let { existingById[it] })
            },
            getChildren = { it.entries }
        )
    }

    fun flattenStyleToEntities(
        problem: Int,
        entries: List<StyleFormEntry>,
        category: FormEntryEntity.FormCategory,
        existingById: Map<Long, FormEntryEntity> = emptyMap()
    ): List<FormEntryEntity> {
        return flattenToEntities(
            entries,
            toEntity = { entry, parent, idx ->
                entry.toEntity(problem, category, parent, idx, entry.id?.let { existingById[it] })
            },
            getChildren = { it.entries }
        )
    }

    fun flattenPenaltyToEntities(
        problem: Int,
        entries: List<PenaltyFormEntry>,
        category: FormEntryEntity.FormCategory,
        existingById: Map<Long, FormEntryEntity> = emptyMap()
    ): List<FormEntryEntity> {
        return flattenToEntities(
            entries,
            toEntity = { entry, parent, idx ->
                entry.toEntity(problem, category, parent, idx, entry.id?.let { existingById[it] })
            },
            getChildren = { it.entries }
        )
    }

    private fun <T> flattenToEntities(
        entries: List<T>,
        toEntity: (T, FormEntryEntity?, Int) -> FormEntryEntity,
        getChildren: (T) -> List<T>
    ): List<FormEntryEntity> {
        val result = mutableListOf<FormEntryEntity>()
        fun process(entry: T, parent: FormEntryEntity?, idx: Int) {
            val entity = toEntity(entry, parent, idx)
            result += entity
            getChildren(entry).forEachIndexed { i, child -> process(child, entity, i) }
        }
        entries.forEachIndexed { idx, entry -> process(entry, null, idx) }
        return result
    }

    fun reconstructLongTermFromEntities(entities: List<FormEntryEntity>): List<LongTermFormEntry> {
        val childrenByParent = buildChildrenMap(entities)
        return entities.filter { it.parent == null }
            .sortedBy { it.orderIndex }
            .map { it.toLongTermFormEntry(childrenByParent) }
    }

    fun reconstructStyleFromEntities(entities: List<FormEntryEntity>): List<StyleFormEntry> {
        val childrenByParent = buildChildrenMap(entities)
        return entities.filter { it.parent == null }
            .sortedBy { it.orderIndex }
            .map { it.toStyleFormEntry(childrenByParent) }
    }

    fun reconstructPenaltyFromEntities(entities: List<FormEntryEntity>): List<PenaltyFormEntry> {
        val childrenByParent = buildChildrenMap(entities)
        return entities.filter { it.parent == null }
            .sortedBy { it.orderIndex }
            .map { it.toPenaltyFormEntry(childrenByParent) }
    }

    private fun buildChildrenMap(entities: List<FormEntryEntity>): Map<Long, List<FormEntryEntity>> {
        return entities.filter { it.parent != null }
            .groupBy { it.parent!!.id }
            .mapValues { (_, children) -> children.sortedBy { it.orderIndex } }
    }

    fun collectAllIds(entries: List<LongTermFormEntry>): Set<Long> {
        return collectIds(entries, getId = { it.id }, getChildren = { it.entries })
    }

    fun collectStyleIds(entries: List<StyleFormEntry>): Set<Long> {
        return collectIds(entries, getId = { it.id }, getChildren = { it.entries })
    }

    fun collectPenaltyIds(entries: List<PenaltyFormEntry>): Set<Long> {
        return collectIds(entries, getId = { it.id }, getChildren = { it.entries })
    }

    private fun <T> collectIds(
        entries: List<T>,
        getId: (T) -> Long?,
        getChildren: (T) -> List<T>
    ): Set<Long> {
        val ids = mutableSetOf<Long>()
        fun collect(entry: T) {
            getId(entry)?.let { ids.add(it) }
            getChildren(entry).forEach { collect(it) }
        }
        entries.forEach { collect(it) }
        return ids
    }
}

