package odyseja.odysejapka.drive

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope

@Configuration
class DriveConfiguration {

    @Bean
    @Profile("!test")
    fun driveAdapter(): DriveAdapter {
        return GDriveAdapter()
    }
} 