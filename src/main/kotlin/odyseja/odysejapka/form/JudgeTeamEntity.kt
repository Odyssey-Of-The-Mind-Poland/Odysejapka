package odyseja.odysejapka.form

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import odyseja.odysejapka.city.CityEntity
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

/**
 * Ta klasa reprezentuje jeden zespół sędziowski – czyli kombinację liczby sędziów A, B i Stylu w danym problemie na danym konkursie.
 */

@Entity
@Table(name = "judge_teams")
class JudgeTeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column
    var judgeCountDtA: Int = 1

    @Column
    var judgeCountDtB: Int = 1

    @Column
    var judgeCountStyle: Int = 1

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH])
    @OnDelete(action = OnDeleteAction.CASCADE)
    var city: CityEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_form_id", referencedColumnName = "id")
    var problemForm: ProblemFormEntity? = null


    companion object {
        fun create(problemForm: ProblemFormEntity, city: CityEntity): JudgeTeamEntity {
            val entity = JudgeTeamEntity()
            entity.problemForm = problemForm
            entity.city = city
            return entity
        }
    }

    fun toJudgeTeam(): JudgeTeam {
        return JudgeTeam(
            problemForm?.problem ?: 0,
            city?.id
                ?: throw IllegalStateException("Ten zespół sędziowski nie ma przypisanego konkursu."),
            JudgeCount(
                judgeCountDtA,
                judgeCountDtB,
                judgeCountStyle
            )
        )
    }
}