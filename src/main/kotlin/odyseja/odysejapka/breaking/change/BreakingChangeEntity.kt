package odyseja.odysejapka.breaking.change

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "breaking_change")
class BreakingChangeEntity(
    @Id
    @Column
    var id: Int,
    @Column
    var version: String
) {
    fun toBreakingChange(): BreakingChange {
        return BreakingChange(
            version = version
        )
    }
}