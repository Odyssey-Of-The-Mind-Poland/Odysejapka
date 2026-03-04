package odyseja.odysejapka.gad

import odyseja.odysejapka.gad.PunctationCells

data class GadGlobalCache(
    val templatesFolderId: String,
    val problemPunctuationCells: Map<String, PunctationCells>
)