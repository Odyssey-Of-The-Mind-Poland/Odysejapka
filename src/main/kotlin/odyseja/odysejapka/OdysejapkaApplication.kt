package odyseja.odysejapka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
class OdysejapkaApplication

fun main(args: Array<String>) {
	runApplication<OdysejapkaApplication>(*args)
}
