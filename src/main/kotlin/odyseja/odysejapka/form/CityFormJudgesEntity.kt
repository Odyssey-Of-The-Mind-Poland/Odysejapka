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
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

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

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH])
    @OnDelete(action = OnDeleteAction.CASCADE)
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