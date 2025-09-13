package odyseja.odysejapka.pdf

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.core.io.ClassPathResource
import java.util.Base64

@Service
class PdfService(
    @Value("\${clsi.base-url}")
    private val clsiUrl: String
): PdfCompiler {

    private val restTemplate: RestTemplate = RestTemplate()
    private val logger = LoggerFactory.getLogger(javaClass)

    open class PdfGenerationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
    class CompilationFailedException(message: String) : PdfGenerationException(message)
    class EmptyPdfException : PdfGenerationException("Generated PDF is empty")
    class NoPdfFileException : PdfGenerationException("No PDF file found in response")

    override fun generate(request: LatexRequest): ByteArray? {
        try {
            val clsiResponse = requestClsi(request)
            validateCompilationStatus(clsiResponse)
            val pdfUrl = clsiResponse?.let { extractPdfUrl(it) }
            return pdfUrl?.let { downloadPdf(it) }
        } catch (e: RestClientException) {
            logger.error("Error communicating with CLSI: ${e.message}", e)
            throw PdfGenerationException("Error communicating with CLSI", e)
        } catch (e: Exception) {
            if (e is PdfGenerationException) throw e
            logger.error("Unexpected error generating PDF: ${e.message}", e)
            throw PdfGenerationException("Unexpected error generating PDF", e)
        }
    }

    private fun validateCompilationStatus(clsiResponse: ClsiResponse?) {
        if (clsiResponse == null) {
            throw PdfGenerationException("Null response received from CLSI")
        }
        
        if (clsiResponse.compile.status != "success") {
            throw CompilationFailedException("CLSI compilation failed with status: ${clsiResponse.compile.status}")
        }
    }

    private fun extractPdfUrl(clsiResponse: ClsiResponse): String {
        val pdfFile = clsiResponse.compile.outputFiles.find { it.type == "pdf" }
            ?: throw NoPdfFileException()
        
        return pdfFile.url
    }

    private fun downloadPdf(pdfUrl: String): ByteArray {
        val pdfResponse = restTemplate.exchange(
            pdfUrl.replace("http://127.0.0.1:3013", clsiUrl),
            HttpMethod.GET,
            null,
            ByteArray::class.java
        )

        val pdfContent = pdfResponse.body
        if (pdfContent == null || pdfContent.isEmpty()) {
            throw EmptyPdfException()
        }
        
        return pdfContent
    }

    private fun requestClsi(request: LatexRequest): ClsiResponse? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val requestBody = createClsiRequestBody(request)
        val requestEntity = HttpEntity(requestBody, headers)
        
        return restTemplate.postForObject(
            "$clsiUrl/project/test/compile", 
            requestEntity, 
            ClsiResponse::class.java
        )
    }

    private fun base64Resource(pathInClasspath: String, pathInProject: String): Map<String, Any> {
        val bytes = ClassPathResource(pathInClasspath).inputStream.use { it.readBytes() }
        val b64 = Base64.getEncoder().encodeToString(bytes)
        return mapOf(
            "path" to pathInProject,
            "content" to b64,
            "encoding" to "base64"
        )
    }

    private fun createClsiRequestBody(request: LatexRequest): Map<String, Any> {
        val resources = mutableListOf<Map<String, Any>>(
            mapOf(
                "path" to "main.tex",
                "content" to request.content.trim()
            )
        )

        val assetBase = "https://grzybek.snet.ovh/static"

        val fontFiles = listOf(
            "Ubuntu-Regular.ttf",
            "Ubuntu-Bold.ttf",
            "Ubuntu-Italic.ttf",
            "Ubuntu-BoldItalic.ttf"
        )

        fontFiles.forEach { f ->
            resources += mapOf(
                "path" to "$f",
                "url"  to "$assetBase/fonts/$f",
                "modified" to (System.currentTimeMillis() / 1000)
            )
        }

        return mapOf(
            "compile" to mapOf(
                "resources" to resources,
                "rootResourcePath" to "main.tex",
                "options" to mapOf(
                    "compiler" to "lualatex",
                    "timeout" to 400
                )
            )
        )
    }


}