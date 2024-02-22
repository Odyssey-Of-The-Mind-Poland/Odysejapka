package odyseja.odysejapka.timetable

import odyseja.odysejapka.domain.*
import javax.persistence.*


@Entity(name = "performance")
class PerformanceEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column()
  var id: Int,
  @ManyToOne(fetch = FetchType.LAZY)
  var cityEntity: CityEntity,
  @Column
  var team: String,
  @ManyToOne(fetch = FetchType.LAZY)
  var problemEntity: ProblemEntity,
  @ManyToOne(fetch = FetchType.LAZY)
  var ageEntity: AgeEntity,
  @ManyToOne(fetch = FetchType.LAZY)
  var stageEntity: StageEntity,
  @Column
  var performance: String,
  @Column
  var spontan: String,
  @Column(columnDefinition = "integer default 0")
  var part: Int,
  @Column
  var performanceDay: String,
  @Column
  var spontanDay: String,
  @Column
  var league: String
) {

  fun toPerformance(): Performance {
    return Performance(
      id = id,
      city = cityEntity.name,
      team = team,
      problem = problemEntity.id,
      age = ageEntity.id,
      stage = stageEntity.number,
      performance = performance,
      spontan = spontan,
      part = part,
      performanceDay = performanceDay,
      spontanDay = spontanDay,
      league = league
    )
  }

  fun toGroup(): PerformanceGroup.Group {
    return PerformanceGroup.Group(
      city = cityEntity.name,
      problem = problemEntity.id,
      age = ageEntity.id,
      stage = stageEntity.number,
      part = part,
      league = league
    )
  }
}