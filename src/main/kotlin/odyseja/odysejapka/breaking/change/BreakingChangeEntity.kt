package odyseja.odysejapka.breaking.change

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "breaking_change")
class BreakingChangeEntity(
    @Id
    @Column
    val id: Int,
    @Column
    var version: String
) {
    fun toBreakingChange(): BreakingChangeController.BreakingChange {
        return BreakingChangeController.BreakingChange(
            version = version
        )
    }
}