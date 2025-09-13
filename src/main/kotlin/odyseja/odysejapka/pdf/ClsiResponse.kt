package odyseja.odysejapka.pdf

data class ClsiResponse(
    val compile: CompileResult
)

data class CompileResult(
    val status: String,
    val outputFiles: List<OutputFile>
)

data class OutputFile(
    val type: String,
    val url: String
) 