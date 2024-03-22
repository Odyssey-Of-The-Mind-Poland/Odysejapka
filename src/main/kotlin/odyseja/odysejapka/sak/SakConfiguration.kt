import com.google.api.client.json.gson.GsonFactory
import odyseja.odysejapka.drive.CredentialsProvider
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.sak.SakRunner
import odyseja.odysejapka.timetable.TimeTableService

internal class SakConfiguration(
    private val templatesFolderId: String,
    private val timetableService: TimeTableService,
    private val zspId: String
) {

    fun sakRunner(): SakRunner {
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val driveAdapter = DriveAdapter(credentials, jsonFactory, templatesFolderId)
        val sheetsAdapter = SheetAdapter(credentials, jsonFactory)
        return SakRunner(driveAdapter, sheetsAdapter, timetableService, zspId)
    }
}