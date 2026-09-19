package odyseja.odysejapka.form

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import odyseja.odysejapka.city.CityEntity

@Entity
@Table(name = "form_problems")
class CityFormJudgesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;

    @Column
    var problem: Int = 0

    @Column
    var judgeCount: Int = 0

    @ManyToOne(cascade = [CascadeType.ALL])
    var city: CityEntity? = null

    companion object {
        fun create(problem: Int, city: CityEntity): CityFormJudgesEntity {
            val entity = CityFormJudgesEntity()
            entity.problem = problem
            entity.city = city
            return entity
        }
    }
}

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
}

