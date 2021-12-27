package odyseja.odysejapka.domain

import javax.persistence.*

@Entity
class ProblemEntity(
        @Id
        @Column()
        val id: Int,
        @Column()
        var name: String
)