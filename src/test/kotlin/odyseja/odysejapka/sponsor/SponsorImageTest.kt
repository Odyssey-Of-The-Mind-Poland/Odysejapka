package odyseja.odysejapka.sponsor

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class SponsorImageTest : OdysejaDsl() {

    lateinit var sponsorClient: SponsorController

    @Autowired
    lateinit var sponsorService: SponsorService

    @BeforeEach
    fun sponsorImageTestSetUp() {
        sponsorClient = controllerClientFactory.create(SponsorController::class.java)
    }

    // Reading the image outside of a transaction fails on Postgres ("Large Objects may not be used in auto-commit mode").
    @Test
    fun `should return the uploaded image bytes`() {
        val city = createCity("Sponsorowo")
        val bytes = byteArrayOf(1, 2, 3, 4, 5)
        val sponsor = sponsorClient.uploadImage(MockMultipartFile("image", "logo.png", "image/png", bytes), 0, city.id)

        assertThat(sponsorService.getImage(sponsor.id)).isEqualTo(bytes)
    }
}
