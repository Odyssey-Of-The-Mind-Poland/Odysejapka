package odyseja.odysejapka.spontan

import jakarta.persistence.*
import odyseja.odysejapka.city.CityEntity

@Entity
@Table(
    name = "spontan_group_assignment",
    uniqueConstraints = [UniqueConstraint(columnNames = ["city_id", "problem", "age", "league"])]
)
class SpontanGroupAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    var city: CityEntity? = null

    @Column(nullable = false)
    var problem: Int = 0

    @Column(nullable = false)
    var age: Int = 0

    @Column
    var league: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spontan_definition_id")
    var spontanDefinition: SpontanDefinitionEntity? = null

    @Column(name = "judge_count", nullable = false)
    var judgeCount: Int = 3
}
