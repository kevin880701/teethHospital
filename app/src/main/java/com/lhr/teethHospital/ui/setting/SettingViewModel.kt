package com.lhr.teethHospital.ui.setting

import android.app.Application
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.googleDrive.GoogleDriveServiceFunction
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseViewModel
import com.lhr.teethHospital.ui.main.MainViewModel
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SettingViewModel(context: Context) :
    BaseViewModel(context) {
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
            GlobalScope.launch(Dispatchers.IO) {

                // 如果已登錄，執行上傳備份操作
                GoogleDriveServiceFunction().uploadFile(settingFragment.requireActivity())
            }
        }
    }


}