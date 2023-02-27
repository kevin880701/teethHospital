package com.lhr.teethHospital.GoogleDrive

import android.R
import android.content.IntentSender
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.drive.DriveId
import com.google.android.gms.drive.OpenFileActivityOptions
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.lhr.teethHospital.File.DeleteFile
import com.lhr.teethHospital.Model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.Model.Model.Companion.BACKUP_NAME
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


internal class GoogleDriveServiceFunction(mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private var mDriveService = mDriveService

    fun uploadFile() {
        Tasks.call(mExecutor) {
            val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-")
            val time: String = recordDate.format(Date())
            var fileMetaData = File()
            fileMetaData.name = time + BACKUP_NAME
            val filePath = java.io.File(APP_FILES_PATH, BACKUP_NAME)
            val mediaContent = FileContent("application/x-rar-compressed", filePath)
            mDriveService.files().create(fileMetaData, mediaContent)
                .setFields("id, parents")
                .execute()
            DeleteFile(APP_FILES_PATH + java.io.File.separator + BACKUP_NAME)
        }
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