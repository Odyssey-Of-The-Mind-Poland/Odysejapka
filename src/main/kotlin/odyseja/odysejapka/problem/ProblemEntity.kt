package odyseja.odysejapka.problem

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "problem")
class ProblemEntity(
    @Id
    @Column()
    var id: Int,
    @Column()
    var name: String
) {
    init {
        validate()
    }

    fun validate() {
        require(id in 0..5) { "Numer problemu musi wynosić od 0 do 5" }
        require(name.isNotBlank()) { "Nazwa problemu nie może być pusta" }
    }
}