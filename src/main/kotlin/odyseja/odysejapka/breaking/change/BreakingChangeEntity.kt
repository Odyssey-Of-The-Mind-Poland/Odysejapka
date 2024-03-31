package odyseja.odysejapka.breaking.change

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

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