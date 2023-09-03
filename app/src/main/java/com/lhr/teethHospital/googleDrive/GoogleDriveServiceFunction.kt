package com.lhr.teethHospital.googleDrive

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Tasks
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.lhr.teethHospital.R
import com.lhr.teethHospital.file.DeleteFile
import com.lhr.teethHospital.model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.model.Model.Companion.BACKUP_NAME
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


internal class GoogleDriveServiceFunction() {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
//    private var mDriveService = mDriveService

    fun uploadFile2(activity: Activity) {
        Tasks.call(mExecutor) {
            val credential =
                GoogleSignIn.getLastSignedInAccount(activity)
                    ?.let { account ->
                        GoogleAccountCredential.usingOAuth2(
                            activity,
                            setOf(DriveScopes.DRIVE_FILE)
                        )
                            .setBackOff(ExponentialBackOff())
                            .setSelectedAccount(account.account)
                    } ?: throw IllegalArgumentException("Google account not found")
            var mDriveService =
                Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                    .setApplicationName(
                        activity.getString(R.string.app_name)
                    )
                    .build()

            val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-")
            val time: String = recordDate.format(Date())
            var fileMetaData = File()
            fileMetaData.name = time + BACKUP_NAME
            val filePath = java.io.File(APP_FILES_PATH, BACKUP_NAME)
            val mediaContent = FileContent("application/x-rar-compressed", filePath)

            try {
                val file = mDriveService.files().create(fileMetaData, mediaContent)
                    .setFields("id, parents")
                    .execute()
                DeleteFile(APP_FILES_PATH + java.io.File.separator + BACKUP_NAME)
                if (file != null) {
                    Toast.makeText(activity, "檔案上傳成功", Toast.LENGTH_LONG).show()
                }
            } catch (e: GoogleJsonResponseException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    fun uploadFile() {
//        Tasks.call(mExecutor) {
//            val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-")
//            val time: String = recordDate.format(Date())
//            var fileMetaData = File()
//            fileMetaData.name = time + BACKUP_NAME
//            val filePath = java.io.File(APP_FILES_PATH, BACKUP_NAME)
//            val mediaContent = FileContent("application/x-rar-compressed", filePath)
//            mDriveService.files().create(fileMetaData, mediaContent)
//                .setFields("id, parents")
//                .execute()
//            DeleteFile(APP_FILES_PATH + java.io.File.separator + BACKUP_NAME)
//        }
    }


    fun downloadFile() {
        Tasks.call(mExecutor) {
//            val openOptions = OpenFileActivityOptions.Builder()
//                .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
//                .setActivityTitle(getString(R.string.select_file))
//                .build()
//            newOpenFileActivityIntentSender(openOptions)
        }

    }
}