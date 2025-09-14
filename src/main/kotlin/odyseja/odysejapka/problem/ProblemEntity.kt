package odyseja.odysejapka.problem

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "problem")
class ProblemEntity(
        @Id
        @Column()
        val id: Int,
        @Column()
        var name: String
)