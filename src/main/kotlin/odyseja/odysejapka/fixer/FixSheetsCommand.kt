package odyseja.odysejapka.fixer

data class FixSheetsCommand(
    val folderId: String,
    val pattern: String,
    val cells: List<ReplacementCell>
) {

    data class ReplacementCell(
        val sheetName: String,
        val cell: String,
        val value: String
    )
}