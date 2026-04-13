package odyseja.odysejapka.change

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.test.context.support.WithMockUser
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class ChangeTest: OdysejaDsl() {
    lateinit var versionClient: VersionController

    @BeforeEach
    fun changeTestSetUp() {
        versionClient = controllerClientFactory.create(VersionController::class.java)
    }

    @Test
    fun `should return a Version object`() {
        val version = versionClient.getVersion()
        Assertions.assertThat(version).isInstanceOf(Version::class.java)
        Assertions.assertThat(version.version).isInstanceOf(Int::class.java)
    }

    @Test
    fun `should increment the version number when updated`() {
        val version1 = versionClient.getVersion().version
        createCity("Stworzenie miasta aktualizuje bazę danych")
        val version2 = versionClient.getVersion().version

        Assertions.assertThat(version1 + 1).isEqualTo(version2)
    }
}