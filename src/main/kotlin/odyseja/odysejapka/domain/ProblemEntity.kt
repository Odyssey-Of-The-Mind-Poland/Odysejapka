package odyseja.odysejapka.domain

import javax.persistence.*

@Entity(name = "problem")
class ProblemEntity(
        @Id
        @Column()
        val id: Int,
        @Column()
        var name: String
)