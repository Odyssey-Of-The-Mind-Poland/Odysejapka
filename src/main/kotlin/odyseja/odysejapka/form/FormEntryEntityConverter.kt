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
                    FormEntry.EntryType.PUNCTUATION -> FormEntryEntity.EntryType.PUNCTUATION
                    FormEntry.EntryType.SECTION -> FormEntryEntity.EntryType.SECTION
                    FormEntry.EntryType.PUNCTUATION_GROUP -> FormEntryEntity.EntryType.PUNCTUATION_GROUP
                }
                this.calcType = entry.calcType
            }

            // Add parent entity first
            result += entity

            // Then process children recursively
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

        fun buildEntry(entity: FormEntryEntity): FormEntry {
            val children = childrenByParent[entity.id] ?: emptyList()

            val entryType = when (entity.entryType) {
                FormEntryEntity.EntryType.PUNCTUATION -> FormEntry.EntryType.PUNCTUATION
                FormEntryEntity.EntryType.SECTION -> FormEntry.EntryType.SECTION
                FormEntryEntity.EntryType.PUNCTUATION_GROUP -> FormEntry.EntryType.PUNCTUATION_GROUP
            }

            return FormEntry(
                id = entity.id,
                name = entity.name,
                type = entryType,
                calcType = entity.calcType,
                entries = children.map { buildEntry(it) }
            )
        }

        return rootEntries.map { buildEntry(it) }
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

