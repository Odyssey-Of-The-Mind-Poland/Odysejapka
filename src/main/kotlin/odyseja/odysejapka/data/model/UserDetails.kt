package odyseja.odysejapka.data.model

import org.springframework.security.core.GrantedAuthority
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserDetails(
        @Id
        @Column(length = 32, unique = true, updatable = true, nullable = false)
        val username: String,

        @Column(length = 160, nullable = false)
        var password: String,

        @Column(length = 20, nullable = true)
        var authority: String
)