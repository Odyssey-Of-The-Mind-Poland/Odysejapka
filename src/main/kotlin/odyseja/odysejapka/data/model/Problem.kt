package odyseja.odysejapka.data.model

import javax.persistence.*

@Entity
class Problem(
        @Id
        @Column()
        val id: Int,
        @Column()
        var name: String
)