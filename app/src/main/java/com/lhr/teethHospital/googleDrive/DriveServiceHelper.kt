package com.lhr.teethHospital.googleDrive

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.util.Pair
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.model.Model.Companion.BACKUP_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 * https://github.com/mesadhan/google-drive-app
 */
class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    /**
     * 在BACKUP_PATH/ZIP_NAME創建檔案並回傳 file ID
     */
    suspend fun createFile(): String {
        return withContext(Dispatchers.IO) {
            val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-")
            val time: String = recordDate.format(Date())
            val metadata = File()
                    .setParents(listOf("root"))
                    .setMimeType("*/*")
                    .setName("$time$BACKUP_NAME")
            val mediaContent = FileContent("*/*", java.io.File(APP_FILES_PATH + java.io.File.separator +  BACKUP_NAME))

            val googleFile =
                mDriveService.files().create(metadata, mediaContent).execute()
                    ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        }
    }

    /**
     * Opens the file identified by `fileId` and returns a [Pair] of its name and
     * contents.
     */
    fun readFile(fileId: String?): Task<Pair<String, String>> {
        return Tasks.call(
            mExecutor
        ) {

            // Retrieve the metadata as a File object.
            val metadata =
                mDriveService.files()[fileId].execute()
            val name = metadata.name
            mDriveService.files()[fileId].executeMediaAsInputStream().use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    val contents = stringBuilder.toString()
                    return@call Pair.create<String, String>(
                        name,
                        contents
                    )
                }
            }
        }
    }

    /**
     * Updates the file identified by `fileId` with the given `name` and `content`.
     */
    fun saveFile(fileId: String?, name: String?, content: String?): Task<Void?> {
        return Tasks.call(
            mExecutor
        ) {

            // Create a File containing any metadata changes.
            val metadata =
                File().setName(name)

            // Convert content to an AbstractInputStreamContent instance.
            val contentStream =
                ByteArrayContent.fromString("text/plain", content)

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute()
            null
        }
    }

    /**
     * Returns a [FileList] containing all the visible files in the user's My Drive.
     *
     *
     * The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the [Google
 * Developer's Console](https://play.google.com/apps/publish) and be submitted to Google for verification.
     */
    fun queryFiles(): Task<FileList> {
        return Tasks.call(
            mExecutor
        ) { mDriveService.files().list().setSpaces("drive").execute() }
    }

    /**
     * Returns an [Intent] for opening the Storage Access Framework file picker.
     */
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        return intent
    }

    /**
     * Opens the file at the `uri` returned by a Storage Access Framework [Intent]
     * created by [.createFilePickerIntent] using the given `contentResolver`.
     */
    fun openFileUsingStorageAccessFramework(
        contentResolver: ContentResolver, uri: Uri?
    ): Task<Pair<String, String>> {
        return Tasks.call(
            mExecutor
        ) {

            // Retrieve the document's display name from its metadata.
            var name: String
            contentResolver.query(uri!!, null, null, null, null).use { cursor ->
                name = if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex =
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.getString(nameIndex)
                } else {
                    throw IOException("Empty cursor returned for file.")
                }
            }

            // Read the document's contents as a String.
            var content: String
            contentResolver.openInputStream(uri).use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    content = stringBuilder.toString()
                }
            }
            Pair.create(name, content)
        }
    }
}