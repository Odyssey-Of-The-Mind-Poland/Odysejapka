package odyseja.odysejapka.users

import odyseja.odysejapka.roles.Role

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val userId: String,
    val roles: List<Role> = emptyList()
)
