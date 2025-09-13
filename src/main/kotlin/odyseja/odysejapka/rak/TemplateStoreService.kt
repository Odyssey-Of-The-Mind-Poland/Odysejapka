package odyseja.odysejapka.rak

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.templatemode.TemplateMode
import java.time.Instant

@Service
class TemplateStoreService(
    private val repo: TemplateRepository
) {
    @Transactional(readOnly = true)
    fun find(name: String): TemplateEntity? = repo.findByName(name)

    @Transactional
    fun getOrCreate(name: String, defaultBody: String): TemplateEntity {
        repo.findByName(name)?.let { return it }

        val entity = TemplateEntity(
            name = name,
            content = defaultBody,
            updatedAt = Instant.now()
        )

        return try {
            repo.saveAndFlush(entity)
        } catch (ex: DataIntegrityViolationException) {
            repo.findByName(name) ?: throw ex
        }
    }

    @Transactional
    fun update(name: String, content: String): TemplateEntity {
        val existing = repo.findByName(name)
            ?: TemplateEntity(name = name, content = content)
        existing.content = content
        existing.updatedAt = Instant.now()
        return repo.save(existing)
    }
}
