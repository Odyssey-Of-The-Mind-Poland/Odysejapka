package odyseja.odysejapka.form

data class ValidationFailure(
    val entryId: Long,
    val rule: String,
    val message: String
)
