package odyseja.odysejapka.domain

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
  var part: Int
)