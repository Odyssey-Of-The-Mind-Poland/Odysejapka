package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ChangeEntity
import odyseja.odysejapka.domain.Version
import odyseja.odysejapka.port.ChangeUseCase
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
internal class ChangeService(private val changeRepository: ChangeRepository) : ChangeUseCase {

  override fun getVersion(): Version {
    val lastChange = changeRepository.findFirstByOrderByChangedAtDesc()
    return Version(lastChange.id)
  }

  override fun updateVersion() {
    changeRepository.save(ChangeEntity(0, Timestamp(System.currentTimeMillis())))
  }
}