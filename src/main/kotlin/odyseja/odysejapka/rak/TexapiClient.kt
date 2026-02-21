package odyseja.odysejapka.rak

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets

data class TexapiCompileRequest(val content: String)

data class TexapiCompileResponse(
    val status: String,
    val errors: List<String>? = null,
    @param:JsonProperty("resultPath") val resultPath: String? = null,
    val outputFiles: List<OutputFile>? = null
)

data class OutputFile(val type: String, val content: String)

@Component
class TexapiClient(
    @Value("\${texapi.base-url:https://texapi.ovh}") private val baseUrl: String,
    @Value("\${texapi.api-key:}") private val apiKey: String,
    private val restTemplate: RestTemplate,
    private val objectMapper: com.fasterxml.jackson.databind.ObjectMapper
) {

    fun compileFilesToPdf(mainTexContent: String, additionalFiles: List<Pair<String, ByteArray>>): ByteArray {
        if (apiKey.isBlank()) {
            throw IllegalStateException("texapi.api-key is not set. Add TEXAPI_API_KEY to environment or texapi.api-key to configuration.")
        }
        val headers = HttpHeaders().apply {
            set("X-API-KEY", apiKey)
            contentType = MediaType.MULTIPART_FORM_DATA
        }
        val body = LinkedMultiValueMap<String, Any>().apply {
            add("files", NamedByteArrayResource(mainTexContent.toByteArray(StandardCharsets.UTF_8), "main.tex"))
            additionalFiles.forEach { (filename, bytes) ->
                add("files", NamedByteArrayResource(bytes, filename))
            }
        }
        val url = "$baseUrl/api/latex/compile/file?compiler=lualatex&mainFile=main.tex"
        val request = HttpEntity(body, headers)
        val response = restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
        val responseBody = response.body ?: throw IllegalStateException("Empty response from Texapi")
        val parsed = objectMapper.readValue(responseBody, TexapiCompileResponse::class.java)
        if (parsed.status == "error") {
            val messages = parsed.errors?.joinToString("; ") ?: "Unknown error"
            throw IllegalStateException("Texapi compilation failed: $messages")
        }
        val path = parsed.resultPath ?: throw IllegalStateException("Texapi returned success but no resultPath")
        return downloadFile(path)
    }

    private fun downloadFile(fileKey: String): ByteArray {
        val headers = HttpHeaders().apply { set("X-API-KEY", apiKey) }
        val request = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(
            "$baseUrl$fileKey",
            HttpMethod.GET,
            request,
            ByteArray::class.java
        )
        return response.body ?: throw IllegalStateException("Empty PDF from Texapi")
    }
}

/** ByteArrayResource with a filename for multipart uploads. */
private class NamedByteArrayResource(
    private val bytes: ByteArray,
    private val filename: String
) : ByteArrayResource(bytes) {
    override fun getFilename(): String = filename
}
