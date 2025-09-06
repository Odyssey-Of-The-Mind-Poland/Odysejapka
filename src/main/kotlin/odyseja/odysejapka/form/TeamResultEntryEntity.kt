package odyseja.odysejapka.form

import odyseja.odysejapka.timetable.PerformanceEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "team_result_entries",
    uniqueConstraints = [javax.persistence.UniqueConstraint(
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