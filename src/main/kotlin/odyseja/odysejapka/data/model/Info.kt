package odyseja.odysejapka.data.model

import javax.persistence.*

@Entity
class Info(
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