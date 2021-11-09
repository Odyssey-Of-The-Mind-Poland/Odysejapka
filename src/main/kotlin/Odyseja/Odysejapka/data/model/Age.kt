package Odyseja.Odysejapka.data.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Age (
        @Id
        @Column()
        val id: Int,
        @Column
        var name: String
)