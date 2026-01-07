package odyseja.odysejapka.form

object FormEntryEntityConverter {

    fun flattenToEntities(
        problem: Int,
        entries: List<FormEntry>,
        category: FormEntryEntity.FormCategory,
        existingById: Map<Long, FormEntryEntity> = emptyMap()
    ): List<FormEntryEntity> {
        val result = mutableListOf<FormEntryEntity>()

        fun processEntry(
            entry: FormEntry,
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
                    FormEntry.EntryType.SCORING -> FormEntryEntity.EntryType.SCORING
                    FormEntry.EntryType.SECTION -> FormEntryEntity.EntryType.SECTION
                    FormEntry.EntryType.SCORING_GROUP -> FormEntryEntity.EntryType.SCORING_GROUP
                    FormEntry.EntryType.STYLE -> FormEntryEntity.EntryType.STYLE
                    FormEntry.EntryType.PENALTY -> FormEntryEntity.EntryType.PENALTY
                }
                when (entry.type) {
                    FormEntry.EntryType.SCORING -> {
                        entry.scoring?.let { p ->
                            this.scoringType = p.scoringType
                            this.pointsMin = p.pointsMin
                            this.pointsMax = p.pointsMax
                            this.judges = p.judges
                            this.noElement = p.noElement
                        }
                    }
                    FormEntry.EntryType.SECTION -> {
                    }
                    FormEntry.EntryType.SCORING_GROUP -> {
                        entry.scoringGroup?.let { pg ->
                            this.pointsMin = pg.pointsMin
                            this.pointsMax = pg.pointsMax
                        }
                    }
                    FormEntry.EntryType.STYLE -> {
                    }
                    FormEntry.EntryType.PENALTY -> {
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


    fun reconstructFromEntities(entities: List<FormEntryEntity>): List<FormEntry> {
        val rootEntries = entities.filter { it.parent == null }
            .sortedBy { it.orderIndex }

        val childrenByParent = entities
            .filter { it.parent != null }
            .groupBy { it.parent!!.id }
            .mapValues { (_, children) -> children.sortedBy { it.orderIndex } }

        return rootEntries.map { it.toFormEntry(childrenByParent) }
    }

    fun collectAllIds(entries: List<FormEntry>): Set<Long> {
        val ids = mutableSetOf<Long>()

        fun collect(entry: FormEntry) {
            entry.id?.let { ids.add(it) }
            entry.entries.forEach { collect(it) }
        }

        entries.forEach { collect(it) }
        return ids
    }
}

