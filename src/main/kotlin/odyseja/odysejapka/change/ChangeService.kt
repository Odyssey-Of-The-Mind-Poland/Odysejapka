package odyseja.odysejapka.change

import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class ChangeService(private val changeRepository: ChangeRepository) {

    fun getVersion(): Version {
        var lastChange = changeRepository.findFirstByOrderByChangedAtDesc()
        if (lastChange == null) {
            updateVersion()
            lastChange = changeRepository.findFirstByOrderByChangedAtDesc()
        }

        return Version(lastChange!!.id)
    }

    fun updateVersion() {
        changeRepository.save(ChangeEntity(0, Timestamp(System.currentTimeMillis())))
    }
}