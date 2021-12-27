package odyseja.odysejapka.domain

import javax.persistence.*

@Entity(name = "info")
class InfoEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column()
        val id: Int,
        @Column()
        val infoName: String,
        @Column()
        val infoText: String,
        @Column()
        val city: String
)