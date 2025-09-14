package odyseja.odysejapka.age

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "age")
class AgeEntity (
        @Id
        @Column
        val id: Int,
        @Column
        var name: String
)