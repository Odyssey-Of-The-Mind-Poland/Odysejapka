package odyseja.odysejapka.users

data class CreateUserRequest(
    val username: String,
    val email: String,
    val userId: String,
    val roles: List<String> = emptyList()
)
