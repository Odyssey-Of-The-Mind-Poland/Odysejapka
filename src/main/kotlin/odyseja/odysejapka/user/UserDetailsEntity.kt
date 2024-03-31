package odyseja.odysejapka.user

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserDetailsEntity(
        @Id
        @Column(length = 32, unique = true, updatable = true, nullable = false)
        val username: String,

        @Column(length = 160, nullable = false)
        var password: String,

        @Column(length = 20, nullable = true)
        var authority: String
)