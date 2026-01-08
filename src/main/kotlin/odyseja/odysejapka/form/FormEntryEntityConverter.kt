package odyseja.odysejapka.form

object FormEntryEntityConverter {

    fun flattenToEntities(
        problem: Int,
        entries: List<LongTermFormEntry>,
        category: FormEntryEntity.FormCategory,
        existingById: Map<Long, FormEntryEntity> = emptyMap()
    ): List<FormEntryEntity> {
        val result = mutableListOf<FormEntryEntity>()

        fun processEntry(
            entry: LongTermFormEntry,
            parent: FormEntryEntity?,
            orderIndex: Int
        ): FormEntryEntity {
            val entity = entry.id?.let { existingById[it] } ?: FormEntryEntity()

            entity.apply {
                this.problem = problem
                this.name = entry.name
                this.formCategory = category
                this.parent = parent
                this.orderIndex = orderIndex
                this.entryType = when (entry.type) {
                    LongTermFormEntry.EntryType.SCORING -> FormEntryEntity.EntryType.SCORING
                    LongTermFormEntry.EntryType.SECTION -> FormEntryEntity.EntryType.SECTION
                    LongTermFormEntry.EntryType.SCORING_GROUP -> FormEntryEntity.EntryType.SCORING_GROUP
                    LongTermFormEntry.EntryType.STYLE -> FormEntryEntity.EntryType.STYLE
                    LongTermFormEntry.EntryType.PENALTY -> FormEntryEntity.EntryType.PENALTY
                }
                when (entry.type) {
                    LongTermFormEntry.EntryType.SCORING -> {
                        entry.scoring?.let { p ->
                            this.scoringType = p.scoringType
                            this.pointsMin = p.pointsMin
                            this.pointsMax = p.pointsMax
                            this.judges = p.judges
                            this.noElement = p.noElement
                        }
                    }
                    LongTermFormEntry.EntryType.SECTION -> {
                    }
                    LongTermFormEntry.EntryType.SCORING_GROUP -> {
                        entry.scoringGroup?.let { pg ->
                            this.pointsMin = pg.pointsMin
                            this.pointsMax = pg.pointsMax
                        }
                    }
                    LongTermFormEntry.EntryType.STYLE -> {
                    }
                    LongTermFormEntry.EntryType.PENALTY -> {
                    }
                }
            }

            result += entity

            entry.entries.forEachIndexed { idx, child ->
                processEntry(child, entity, idx)
            }

            return entity
        }

        entries.forEachIndexed { idx, entry ->
            processEntry(entry, null, idx)
        }

        return result
    }


    fun reconstructFromEntities(entities: List<FormEntryEntity>): List<LongTermFormEntry> {
        val rootEntries = entities.filter { it.parent == null }
            .sortedBy { it.orderIndex }

        val childrenByParent = entities
            .filter { it.parent != null }
            .groupBy { it.parent!!.id }
            .mapValues { (_, children) -> children.sortedBy { it.orderIndex } }

        return rootEntries.map { it.toFormEntry(childrenByParent) }
    }

    fun collectAllIds(entries: List<LongTermFormEntry>): Set<Long> {
        val ids = mutableSetOf<Long>()

        fun collect(entry: LongTermFormEntry) {
            entry.id?.let { ids.add(it) }
            entry.entries.forEach { collect(it) }
        }

        entries.forEach { collect(it) }
        return ids
    }
}

