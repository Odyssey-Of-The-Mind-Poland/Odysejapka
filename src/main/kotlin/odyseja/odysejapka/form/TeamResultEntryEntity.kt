package odyseja.odysejapka.form

import odyseja.odysejapka.timetable.PerformanceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

enum class JudgeType {
    DT_A, DT_B, STYLE
}

@Entity
@Table(name = "team_result_entries",
    uniqueConstraints = [jakarta.persistence.UniqueConstraint(
        columnNames = ["performance_entity_id", "form_entry_entity_id", "judge_type", "judge"]
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

    @Column(name = "judge_type")
    @Enumerated(EnumType.STRING)
    var judgeType: JudgeType = JudgeType.DT_A

    @Column
    var judge: Int = 0

    @Column
    var result: Long = 0

    @Column(name = "no_element")
    var noElement: Boolean? = null

    @Column(name = "style_name")
    var styleName: String? = null

    @Column(name = "zero_balsa")
    var zeroBalsa: Boolean? = null
}