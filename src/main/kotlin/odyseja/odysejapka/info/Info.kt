package odyseja.odysejapka.info

data class Info(
  val id: Int,
  val infoName: String,
  val infoText: String,
  val city: Int,
  val category: Int,
  val sortNumber: Int,
  val categoryName: String,
  val icon: String? = null,
  val color: String? = null
)