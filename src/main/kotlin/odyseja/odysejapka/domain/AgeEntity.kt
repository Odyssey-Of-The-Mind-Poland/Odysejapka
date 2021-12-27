package odyseja.odysejapka.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class AgeEntity (
        @Id
        @Column()
        val id: Int,
        @Column
        var name: String
)