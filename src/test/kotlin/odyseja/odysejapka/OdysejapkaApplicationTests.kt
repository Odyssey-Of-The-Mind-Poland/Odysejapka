package odyseja.odysejapka

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@ComponentScan(basePackages = ["odyseja.odysejapka"])
@ActiveProfiles("test")
class OdysejapkaApplicationTests {

	@Test
	fun contextLoads() {
	}

}
