package odyseja.odysejapka.form

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import odyseja.odysejapka.city.CityEntity

@Entity
@Table(name = "form_problems")
class FormProblemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;

    @Column
    var problem: Int = 0

    @Column
    var judgeCount: Int = 0

    @ManyToOne(cascade = [CascadeType.ALL])
    var city: CityEntity? = null
}