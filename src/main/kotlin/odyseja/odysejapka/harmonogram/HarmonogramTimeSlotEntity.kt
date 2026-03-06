package odyseja.odysejapka.harmonogram

import odyseja.odysejapka.stage.StageEntity
import odyseja.odysejapka.timetable.PerformanceEntity
import jakarta.persistence.*

@Entity(name = "harmonogram_time_slot")
class HarmonogramTimeSlotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val id: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    var stageEntity: StageEntity,
    @Column
    var startTime: String,
    @Column
    var endTime: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    var performanceEntity: PerformanceEntity? = null
)
