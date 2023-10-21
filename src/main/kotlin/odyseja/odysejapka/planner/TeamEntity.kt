package odyseja.odysejapka.planner

import javax.persistence.*

@Entity(name = "team")
class TeamEntity(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int,
    @Column
    var name: String
) {
    fun toPerformance(): Performance {
        return Performance(
            id = id.toLong(),
            teamName = name
        )
    }
}