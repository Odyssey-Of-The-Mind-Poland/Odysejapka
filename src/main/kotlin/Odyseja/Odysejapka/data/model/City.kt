package Odyseja.Odysejapka.data.model

import javax.persistence.*

@Entity
class City(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column()
        val id: Int,
        @Column()
        val name: String
)