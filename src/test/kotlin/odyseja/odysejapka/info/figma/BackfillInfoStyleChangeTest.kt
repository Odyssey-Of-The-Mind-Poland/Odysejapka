package odyseja.odysejapka.info.figma

import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.change.ChangeService
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
class BackfillInfoStyleChangeTest : OdysejaDsl() {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var changeService: ChangeService

    lateinit var infoClient: InfoController

    @BeforeEach
    fun backfillSetUp() {
        infoClient = controllerClientFactory.create(InfoController::class.java)
        // The backfill is global, so infos left by other tests must not take part.
        dataSource.connection.use { it.createStatement().use { statement -> statement.executeUpdate("DELETE FROM info") } }
        infoClient.getInfoCategories().forEach { infoClient.deleteInfoCategory(it.id) }
    }

    @Test
    fun `should style infos after the figma design and leave other infos and manual styles alone`() {
        val city = createCity(uniqueCity("Gdynia"))
        add(city.id, "Gdzie, kiedy i co?")
        add(city.id, "Dojazd i parkingi")
        add(city.id, "Immergas Party")
        add(city.id, "Zasady zachowania", icon = "sun", color = "red")
        add(city.id, "Atrakcje na Finale", color = "purple")

        val updated = dataSource.connection.use { BackfillInfoStyleChange().backfill(it, InfoNameMatcher()) }

        val byName = infoClient.getInfo(city.id).filterNotNull().associateBy { it.infoName }
        assertThat(updated).isEqualTo(3)
        assertThat(byName.getValue("Gdzie, kiedy i co?").icon).isEqualTo("map-01")
        assertThat(byName.getValue("Gdzie, kiedy i co?").color).isEqualTo("blue")
        assertThat(byName.getValue("Dojazd i parkingi").icon).isEqualTo("car-01")
        assertThat(byName.getValue("Dojazd i parkingi").color).isEqualTo("orange")
        assertThat(byName.getValue("Immergas Party").icon).isNull()
        assertThat(byName.getValue("Immergas Party").color).isNull()
        // Own style is never overwritten, a missing half is filled in.
        assertThat(byName.getValue("Zasady zachowania").icon).isEqualTo("sun")
        assertThat(byName.getValue("Zasady zachowania").color).isEqualTo("red")
        assertThat(byName.getValue("Atrakcje na Finale").icon).isEqualTo("rocket-01")
        assertThat(byName.getValue("Atrakcje na Finale").color).isEqualTo("purple")
    }

    @Test
    fun `should do nothing when run again`() {
        val city = createCity(uniqueCity("Sopot"))
        add(city.id, "Szatnia i jedzenie")

        val first = dataSource.connection.use { BackfillInfoStyleChange().backfill(it, InfoNameMatcher()) }
        val second = dataSource.connection.use { BackfillInfoStyleChange().backfill(it, InfoNameMatcher()) }

        assertThat(first).isEqualTo(1)
        assertThat(second).isEqualTo(0)
    }

    @Test
    fun `should bump the data version when run as a liquibase change`() {
        val city = createCity(uniqueCity("Hel"))
        add(city.id, "Wstęp na występy")
        val before = changeService.getVersion().version

        dataSource.connection.use { connection ->
            val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
            BackfillInfoStyleChange().execute(database)
            // Liquibase switches the connection to manual commit and commits after each changeset.
            database.commit()
        }

        assertThat(changeService.getVersion().version).isGreaterThan(before)
        assertThat(infoClient.getInfo(city.id).single()!!.icon).isEqualTo("ticket-01")
    }

    private fun uniqueCity(name: String) = "$name ${UUID.randomUUID().toString().take(6)}"

    private fun add(cityId: Int, name: String, icon: String? = null, color: String? = null) =
        infoClient.addInfo(
            Info(
                id = 0,
                infoName = name,
                infoText = "Opis",
                city = cityId,
                category = 0,
                sortNumber = 0,
                categoryName = "Dla wszystkich",
                icon = icon,
                color = color
            )
        )
}
