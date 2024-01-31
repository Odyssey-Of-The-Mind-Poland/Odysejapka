package odyseja.odysejapka.gad

import PunctationCells

data class GenerateGadCommand(
    val templatesFolderId: String,
    val destinationFolderId: String,
    val zspId: String,
    val problemPunctuationCells: Map<String, PunctationCells>
)
