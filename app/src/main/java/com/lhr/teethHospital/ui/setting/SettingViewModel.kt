package com.lhr.teethHospital.ui.setting

import android.app.Application
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.lhr.teethHospital.R
import com.lhr.teethHospital.googleDrive.GoogleDriveServiceFunction
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val REQUEST_SIGNIN_CODE = 400

    fun uploadBackup(settingFragment: SettingFragment) {
        // 檢查是否已登錄，如果未登錄，則啟動 Google Sign-In 流程
        val account = GoogleSignIn.getLastSignedInAccount(settingFragment.requireActivity())
        if (account == null) {
            val signInIntent = GoogleSignIn.getClient(
                settingFragment.requireActivity(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .build()
            ).signInIntent
            settingFragment.uploadBackupResult.launch(signInIntent)
        } else {
            // 如果已登錄，執行上傳備份操作
            GoogleDriveServiceFunction().uploadFile2(settingFragment.requireActivity())
        }
    }


}