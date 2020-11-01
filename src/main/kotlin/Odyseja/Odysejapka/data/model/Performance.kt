package Odyseja.Odysejapka.data.model

import javax.persistence.*


@Entity
class Performance(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column()
        val id: Int,
        @Column
        val city: String,
        @Column
        val team: String,
        @Column
        val problem: String,
        @Column
        val age: String,
        @Column
        val stage: String,
        @Column
        val performance: String,
        @Column
        val spontan: String
)