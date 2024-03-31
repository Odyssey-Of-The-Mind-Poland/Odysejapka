package odyseja.odysejapka.problem

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "problem")
class ProblemEntity(
        @Id
        @Column()
        val id: Int,
        @Column()
        var name: String
)