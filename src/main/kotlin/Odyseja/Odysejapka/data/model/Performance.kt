package Odyseja.Odysejapka.data.model

import javax.persistence.*


@Entity
class Performance(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column()
        var id: Int,
        @Column
        var city: String,
        @Column
        var team: String,
        @ManyToOne(fetch=FetchType.LAZY)
        var problem: Problem,
        @ManyToOne(fetch=FetchType.LAZY)
        var age: Age,
        @ManyToOne(fetch=FetchType.LAZY)
        var stage: Stage,
        @Column
        var performance: String,
        @Column
        var spontan: String
)