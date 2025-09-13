package odyseja.odysejapka.rak

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : JpaRepository<TemplateEntity, Long> {
    fun findByName(name: String): TemplateEntity?
}
