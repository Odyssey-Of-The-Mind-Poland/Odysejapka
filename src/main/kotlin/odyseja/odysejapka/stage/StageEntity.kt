package odyseja.odysejapka.stage

import odyseja.odysejapka.city.CityEntity
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity(name = "stage")
class StageEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column
        val id: Int,
        @Column
        val number: Int,
        @Column
        var name: String,
        @ManyToOne(fetch=FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        var cityEntity: CityEntity
)