package odyseja.odysejapka.domain

data class Info(
  val id: Int,
  val infoName: String,
  val infoText: String,
  val city: Int,
  val category: Int,
  val sortNumber: Int,
  val categoryName: String
)