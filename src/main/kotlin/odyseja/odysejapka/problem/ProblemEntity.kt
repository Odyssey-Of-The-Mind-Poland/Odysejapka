package odyseja.odysejapka.problem

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import odyseja.odysejapka.info.InfoCategoryStyle

@Entity(name = "problem")
class ProblemEntity(
    @Id
    @Column()
    var id: Int,
    @Column()
    var name: String,
    @Column(columnDefinition = "TEXT")
    @field:Size(max = InfoCategoryStyle.MAX_ICON_LENGTH, message = InfoCategoryStyle.ICON_MESSAGE)
    @field:Pattern(regexp = InfoCategoryStyle.ICON_PATTERN, message = InfoCategoryStyle.ICON_MESSAGE)
    var icon: String? = null,
    @Column()
    @field:Pattern(regexp = InfoCategoryStyle.COLOR_PATTERN, message = InfoCategoryStyle.COLOR_MESSAGE)
    var color: String? = null
) {
    @PrePersist
    fun validate() {
        require(id in 0..5) { "Numer problemu musi wynosić od 0 do 5" }
        require(name.isNotBlank()) { "Nazwa problemu nie może być pusta" }
    }
}
