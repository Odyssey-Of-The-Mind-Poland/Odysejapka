package odyseja.odysejapka.info.content

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.info.Info
import odyseja.odysejapka.info.InfoController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import java.util.UUID
import javax.sql.DataSource

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class SanitizeInfoTextChangeTest : OdysejaDsl() {

    @Autowired
    lateinit var dataSource: DataSource

    lateinit var infoClient: InfoController

    @BeforeEach
    fun sanitizeSetUp() {
        infoClient = controllerClientFactory.create(InfoController::class.java)
        // The step is global, so infos left by other tests must not take part.
        dataSource.connection.use { it.createStatement().use { statement -> statement.executeUpdate("DELETE FROM info") } }
        infoClient.getInfoCategories().forEach { infoClient.deleteInfoCategory(it.id) }
    }

    @Test
    fun `should sanitize the dirty texts and leave the clean ones alone`() {
        val city = createCity(uniqueCity("Gdynia"))
        val dirty = add(city.id, "Brudny", "x")
        val clean = add(city.id, "Czysty", "<p>Tekst</p>")
        setRawText(dirty.id, """<span style="color:rgb(0,0,0);font-weight:700">Ważne</span>""")

        val updated = dataSource.connection.use { SanitizeInfoTextChange().sanitizeAll(it) }

        val byName = infoClient.getInfo(city.id).filterNotNull().associateBy { it.infoName }
        assertThat(updated).isEqualTo(1)
        assertThat(byName.getValue("Brudny").infoText).isEqualTo("<strong>Ważne</strong>")
        assertThat(byName.getValue("Czysty").infoText).isEqualTo(clean.infoText)
    }

    @Test
    fun `should do nothing when run again`() {
        val city = createCity(uniqueCity("Sopot"))
        val info = add(city.id, "Brudny", "x")
        setRawText(info.id, """<p style="text-align:center">Tekst</p>""")

        val first = dataSource.connection.use { SanitizeInfoTextChange().sanitizeAll(it) }
        val second = dataSource.connection.use { SanitizeInfoTextChange().sanitizeAll(it) }

        assertThat(first).isEqualTo(1)
        assertThat(second).isEqualTo(0)
    }

    private fun setRawText(id: Int, text: String) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE info SET info_text = ? WHERE id = ?").use {
                it.setString(1, text)
                it.setInt(2, id)
                it.executeUpdate()
            }
        }
    }

    private fun uniqueCity(name: String) = "$name ${UUID.randomUUID().toString().take(6)}"

    private fun add(cityId: Int, name: String, text: String) =
        infoClient.addInfo(
            Info(
                id = 0,
                infoName = name,
                infoText = text,
                city = cityId,
                category = 0,
                sortNumber = 0,
                categoryName = "Dla wszystkich",
            ),
        )
}
