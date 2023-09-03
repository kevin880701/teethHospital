package com.lhr.teethHospital.googleDrive

//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.model.File
import java.io.IOException
import java.util.*


class UploadBasic {
    /**
     * Upload new file.
     *
     * @return Inserted file metadata if successful, `null` otherwise.
     * @throws IOException if service account credentials file not found.
     */
    @Throws(IOException::class)
    fun uploadBasic(): String {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
        val credentials: GoogleCredentials = GoogleCredentials.getApplicationDefault()
            .createScoped(listOf(DriveScopes.DRIVE_FILE))


        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(
            credentials
        )

        // Build a new authorized API client service.
        val service = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer
        )
            .setApplicationName("Drive samples")
            .build()
        // Upload file photo.jpg on drive.
        val fileMetadata = File()
        fileMetadata.name = "photo.jpg"
        // File's content.
        val filePath = java.io.File("files/photo.jpg")
        // Specify media type and file-path for file.
        val mediaContent = FileContent("image/jpeg", filePath)
        return try {
            val file: File = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()
            println("File ID: " + file.id)
            file.id
        } catch (e: GoogleJsonResponseException) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to upload file: " + e.details)
            throw e
        }
    }
}