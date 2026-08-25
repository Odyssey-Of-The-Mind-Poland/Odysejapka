package odyseja.odysejapka.timetable

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PrePersist

@Entity(name = "age")
class AgeEntity (
    @Id
    @Column
    var id: Int,
    @Column
    var name: String
) {
    @PrePersist
    fun validate() {
        require(id in 0..4) { "Dozwolone ID grupy wiekowej to: 0, 1, 2, 3 lub 4" }
        require(name == id.toString()) {"Nazwa grupy wiekowej musi być równa jej ID."}
    }
}