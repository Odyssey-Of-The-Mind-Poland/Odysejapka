package odyseja.odysejapka.form

import odyseja.odysejapka.timetable.PerformanceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "team_result_entries",
    uniqueConstraints = [jakarta.persistence.UniqueConstraint(
        columnNames = ["performance_entity_id", "form_entry_entity_id", "judge"]
    )])
class TeamResultEntryEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;

    @ManyToOne
    @JoinColumn(name = "performance_entity_id")
    var performanceEntity: PerformanceEntity? = null;

    @ManyToOne
    @JoinColumn(name = "form_entry_entity_id")
    var formEntryEntity: FormEntryEntity? = null;

    @Column
    var judge: Int = 0

    @Column
    var result: Long = 0
}