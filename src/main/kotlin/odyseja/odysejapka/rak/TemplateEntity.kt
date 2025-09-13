package odyseja.odysejapka.rak

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "thymeleaf_templates",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
data class TemplateEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 255)
    val name: String,

    @Lob
    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)
