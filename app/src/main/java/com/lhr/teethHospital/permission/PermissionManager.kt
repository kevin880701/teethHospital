package com.lhr.teethHospital.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionManager(mActivity: Activity) {
    var mActivaty = mActivity

    companion object {
         const val CAMERA = Manifest.permission.CAMERA
         const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
         const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    }
    fun isPermissionGranted(name: String) = ContextCompat.checkSelfPermission(
        mActivaty, name
    ) == PackageManager.PERMISSION_GRANTED


    fun isCameraPermissionGranted() = ContextCompat.checkSelfPermission(
        mActivaty, CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    fun isWriteExternalStoragePermissionGranted() = ContextCompat.checkSelfPermission(
        mActivaty, WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    fun isReadExternalStoragePermissionGranted() = ContextCompat.checkSelfPermission(
        mActivaty, READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

}