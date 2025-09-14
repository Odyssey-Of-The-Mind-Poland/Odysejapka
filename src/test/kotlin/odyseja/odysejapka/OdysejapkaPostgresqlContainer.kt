package odyseja.odysejapka

import org.testcontainers.containers.PostgreSQLContainer

class OdysejapkaPostgresqlContainer private constructor() :
    PostgreSQLContainer<OdysejapkaPostgresqlContainer?>(
        IMAGE_VERSION
    ) {
        override fun start() {
            super.start()
            System.setProperty("DB_URL", container!!.jdbcUrl)
            System.setProperty("DB_USERNAME", container!!.username)
            System.setProperty("DB_PASSWORD", container!!.password)
        }

        override fun stop() {
            // do nothing, JVM handles shut down
        }

        companion object {
            private const val IMAGE_VERSION = "postgres:11.1"
            private var container: OdysejapkaPostgresqlContainer? = null

            val instance: OdysejapkaPostgresqlContainer?
                get() {
                    if (container == null) {
                        container = OdysejapkaPostgresqlContainer()
                    }
                    return container
                }
        }
    }
