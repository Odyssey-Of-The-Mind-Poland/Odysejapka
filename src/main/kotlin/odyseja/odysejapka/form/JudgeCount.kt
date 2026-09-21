package odyseja.odysejapka.form

/**
 * Ta klasa reprezentuje wyłącznie abstrakcyjną kombinację sędziów A, B i Stylu
 * bez przypisywania jej do żadnego konkursu.
 */
data class JudgeCount(
    val dtA: Int = 0,
    val dtB: Int = 0,
    val style: Int = 0
)