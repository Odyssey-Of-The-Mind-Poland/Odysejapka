package odyseja.odysejapka.timetable

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "age")
class AgeEntity (
    @Id
    @Column
    var id: Int,
    @Column
    var name: String
) {
    fun validate() {
        require(id in 0..4) { "Grupa wiekowa może mieć następujące ID: 0, 1, 2, 3, 4" }
        require(name == id.toString()) {"Nazwa grupy wiekowej musi być równa jej ID."}
    }
}