package odyseja.odysejapka

import odyseja.odysejapka.form.FormController
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@ComponentScan(basePackages = ["odyseja.odysejapka"])
@ActiveProfiles("test")
open class OdysejaDsl {

    lateinit var formClient: FormController

    @Autowired
    lateinit var controllerClientFactory: ControllerClientFactory

    @BeforeEach
    fun setUp() {
        formClient = controllerClientFactory.create(FormController::class.java)
    }

}
