package odyseja.odysejapka.port

import odyseja.odysejapka.domain.Version

interface ChangeUseCase {

  fun getVersion(): Version

  fun updateVersion()
}