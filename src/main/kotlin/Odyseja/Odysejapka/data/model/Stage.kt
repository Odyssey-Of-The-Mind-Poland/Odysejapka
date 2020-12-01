package Odyseja.Odysejapka.data.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Stage (
        @Id
        @Column()
        val id: Int,
        @Column
        val name: String
)