package odyseja.odysejapka.city

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import javax.sql.DataSource

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class NormalizeCityLevelMigrationTest : OdysejaDsl() {

    @Autowired
    lateinit var dataSource: DataSource

    @BeforeEach
    fun levelSetUp() {
        clearCities()
    }

    @Test
    fun `should make the finals FINAL and other cities without a level REGIONAL`() {
        val finalCity = createCity("Finał", KonkursLevel.REGIONAL)
        val latin = createCity("FINAL", KonkursLevel.REGIONAL)
        val poznan = createCity("Poznań", KonkursLevel.FINAL)
        val gdynia = createCity("Gdynia", KonkursLevel.FINAL)
        clearLevel(finalCity.id, latin.id, poznan.id)

        runMigration()

        val byName = cityClient.getCities().filterNotNull().associateBy { it.name }
        assertThat(byName.getValue("Finał").level).isEqualTo(KonkursLevel.FINAL)
        assertThat(byName.getValue("FINAL").level).isEqualTo(KonkursLevel.FINAL)
        assertThat(byName.getValue("Poznań").level).isEqualTo(KonkursLevel.REGIONAL)
        // A level that is already set stays, unless the city is the final.
        assertThat(byName.getValue("Gdynia").level).isEqualTo(KonkursLevel.FINAL)
        assertThat(gdynia.id).isNotNull
    }

    @Test
    fun `should be safe to run again`() {
        val city = createCity("Sopot", KonkursLevel.REGIONAL)
        clearLevel(city.id)

        runMigration()
        val afterFirst = cityClient.getCity(city.id).level
        runMigration()

        assertThat(afterFirst).isEqualTo(KonkursLevel.REGIONAL)
        assertThat(cityClient.getCity(city.id).level).isEqualTo(KonkursLevel.REGIONAL)
    }

    @Test
    fun `should return REGIONAL for a city stored without a level`() {
        val city = createCity("Kraków", KonkursLevel.FINAL)
        clearLevel(city.id)

        assertThat(cityClient.getCity(city.id).level).isEqualTo(KonkursLevel.REGIONAL)
        assertThat(cityClient.getCities().filterNotNull().single { it.id == city.id }.level).isEqualTo(KonkursLevel.REGIONAL)
    }

    /** Runs the SQL of changeset 021 as Liquibase would on PostgreSQL; the test database is built by Hibernate, so Liquibase skips it. */
    private fun runMigration() {
        val xml = javaClass.getResourceAsStream("/db/changelog/021-normalize-city-level.xml")!!.use { String(it.readBytes()) }
        val sql = Regex("<sql[^>]*>(.*?)</sql>", RegexOption.DOT_MATCHES_ALL).find(xml)!!.groupValues[1]
        dataSource.connection.use { connection -> connection.createStatement().use { it.execute(sql) } }
    }

    private fun clearLevel(vararg ids: Int) {
        dataSource.connection.use { connection ->
            ids.forEach { connection.createStatement().use { statement -> statement.executeUpdate("UPDATE city SET level = NULL WHERE id = $it") } }
        }
    }
}
