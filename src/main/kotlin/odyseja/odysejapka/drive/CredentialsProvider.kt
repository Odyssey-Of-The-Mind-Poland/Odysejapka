package odyseja.odysejapka.drive

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.drive.DriveScopes

class CredentialsProvider {

    fun getCredentials(): GoogleCredential {
        return GoogleCredential.getApplicationDefault()
            .createScoped(listOf(DriveScopes.DRIVE))
    }

}