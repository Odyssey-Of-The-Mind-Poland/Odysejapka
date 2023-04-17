package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ChangeEntity
import odyseja.odysejapka.domain.Version
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class ChangeService(private val changeRepository: ChangeRepository) {

  fun getVersion(): Version {
    val lastChange = changeRepository.findFirstByOrderByChangedAtDesc()
    return Version(lastChange.id)
  }

  fun updateVersion() {
    changeRepository.save(ChangeEntity(0, Timestamp(System.currentTimeMillis())))
  }
}