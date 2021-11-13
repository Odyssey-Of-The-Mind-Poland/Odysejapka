package odyseja.odysejapka.data.model

import javax.persistence.*

@Entity
class Stage (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column()
        val id: Int,
        @Column
        val number: Int,
        @Column
        var name: String,
        @ManyToOne(fetch=FetchType.LAZY)
        var city: City
)