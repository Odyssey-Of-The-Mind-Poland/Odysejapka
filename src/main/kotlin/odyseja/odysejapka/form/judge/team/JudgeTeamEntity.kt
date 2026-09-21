package odyseja.odysejapka.form.judge.team

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.form.JudgeCount

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
    var problem: Int = 0

    @Column
    var judgeCountDtA: Int = 0

    @Column
    var judgeCountDtB: Int = 0

    @Column
    var judgeCountStyle: Int = 0

    @ManyToOne(cascade = [CascadeType.ALL])
    var city: CityEntity? = null

    companion object {
        fun create(problem: Int, city: CityEntity): JudgeTeamEntity {
            val entity = JudgeTeamEntity()
            entity.problem = problem
            entity.city = city
            return entity
        }
    }

    fun toJudgeTeam(): JudgeTeam {
        return JudgeTeam(
            problem,
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