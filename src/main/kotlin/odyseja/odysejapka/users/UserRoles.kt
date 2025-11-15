package odyseja.odysejapka.users

import odyseja.odysejapka.roles.Role

data class UserRoles(val userId: Long, val roles: List<Role>)