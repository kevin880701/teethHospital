package com.lhr.teethHospital.ui.cover

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.data.cover.CoverRepository
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.permission.PermissionManager
import com.lhr.teethHospital.ui.login.LoginActivity
import java.io.File

class CoverViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        val PERMISSION_REQUEST_CODE = 100
    }

    var coverRepository = CoverRepository(application)
    var isDataGet: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }

    fun getHospitalInfo() {
        coverRepository.fetchHospitalInfo()
        isDataGet.value = true
    }
    fun creeateFolder() {
        val dcimDir = File(TEETH_DIR)
        if (!dcimDir.exists()) {
            dcimDir.mkdir()
        }

        //掃描全部資料夾檔案
        Model.allFileList = dcimDir.listFiles()
    }

    fun checkPermission(activity: Activity){
        var permissionManager = PermissionManager(activity)
        if (permissionManager.isCameraPermissionGranted() &&
            permissionManager.isWriteExternalStoragePermissionGranted() &&
            permissionManager.isReadExternalStoragePermissionGranted()
        ) {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        } else {
            // 如果沒有權限，您可以向使用者要求該權限
            ActivityCompat.requestPermissions(
                activity, *arrayOf(
                    PermissionManager.WRITE_EXTERNAL_STORAGE,
                    PermissionManager.READ_EXTERNAL_STORAGE,
                    PermissionManager.CAMERA
                ), PERMISSION_REQUEST_CODE
            )
        }
    }
}