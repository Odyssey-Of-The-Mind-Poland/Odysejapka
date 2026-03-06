package odyseja.odysejapka.harmonogram

import odyseja.odysejapka.timetable.PerformanceEntity
import jakarta.persistence.*

@Entity(name = "spontan_slot")
class SpontanSlotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val id: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    var spontanRoomEntity: SpontanRoomEntity,
    @Column
    var startTime: String,
    @Column
    var endTime: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    var performanceEntity: PerformanceEntity? = null
)
