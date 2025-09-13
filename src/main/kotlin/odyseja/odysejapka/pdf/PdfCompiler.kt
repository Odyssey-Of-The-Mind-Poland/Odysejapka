package odyseja.odysejapka.pdf

interface PdfCompiler {

    fun generate(request: LatexRequest): ByteArray?
}