package com.lhr.teethHospital.util.Setting

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.DriveFolder
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.lhr.teethHospital.GoogleDrive.GoogleDriveServiceFunction
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import java.util.*


class SettingPresenter(settingFragment: SettingFragment) {
    var settingFragment = settingFragment
    private val REQUEST_SIGNIN_CODE = 400
//    var googleDriveServiceFunction: GoogleDriveServiceFunction? = null

    fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope(DriveScopes.DRIVE_FILE),
                Scope(DriveScopes.DRIVE_APPDATA),
                Scope(DriveScopes.DRIVE)
            )
            .build()
        var client = GoogleSignIn.getClient(mainActivity, signInOptions)
//        startActivityForResult(mainActivity, client.signInIntent, REQUEST_SIGNIN_CODE, null)
        settingFragment.uploadBackupResult.launch(client.signInIntent)
    }

    fun requestStoragePremission() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    fun handleSignInIntent(data: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener { googleSignInAccount: GoogleSignInAccount ->
                val credential = GoogleAccountCredential
                    .usingOAuth2(mainActivity, Collections.singleton(DriveScopes.DRIVE_FILE))
                credential.selectedAccount = googleSignInAccount.account
                drive(credential)
            }
            .addOnFailureListener { e: Exception -> e.printStackTrace() }
    }

    fun drive(credential: GoogleAccountCredential?) {
        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("Appname")
            .build()
        GoogleDriveServiceFunction(googleDriveService).uploadFile()
    }

//    fun downloadBackup() {
//        val intentSender: IntentSender = Drive.DriveApi.newOpenFileActivityBuilder()
//            .setMimeType(arrayOf(DriveFolder.MIME_TYPE)) // <--- FOLDER
//            //.setMimeType(new String[] { "text/plain", "text/html" }) // <- TEXT FILES
//            .build(getGoogleApiClient())
//    }

}