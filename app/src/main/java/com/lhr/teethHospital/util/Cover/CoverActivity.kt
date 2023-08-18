/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.lhr.teethHospital.util.Cover

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.Model.Model.Companion.DATABASES_PATH
import com.lhr.teethHospital.Model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.Model.Model.Companion.allFileList
import com.lhr.teethHospital.Permission.PermissionManager
import com.lhr.teethHospital.Permission.PermissionManager.Companion.CAMERA
import com.lhr.teethHospital.Permission.PermissionManager.Companion.READ_EXTERNAL_STORAGE
import com.lhr.teethHospital.Permission.PermissionManager.Companion.WRITE_EXTERNAL_STORAGE
import com.lhr.teethHospital.R
import com.lhr.teethHospital.util.Main.MainActivity
import java.io.File


class CoverActivity : AppCompatActivity() {
    lateinit var mActivity: Activity
    lateinit var presenter: CoverPresenter
    lateinit var imageMusicCover: ImageView
    lateinit var constrain: ConstraintLayout
    lateinit var permissionManager: PermissionManager
    var PERMISSION_REQUEST_CODE = 100
    var isSetting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        setContentView(R.layout.activity_cover)
        supportActionBar!!.hide()

        mActivity = this

        val intent = Intent(mActivity, MainActivity::class.java)
        // 創建Model
        Model

        APP_FILES_PATH = getExternalFilesDir(null)!!.absolutePath
        DATABASES_PATH = this.getCacheDir().parent + "/databases"
        TEETH_DIR = getExternalFilesDir(null)!!.absolutePath.toString() + "/teeth/"
        // 在DCIM中創建存圖資料夾
        val dcimDir = File(TEETH_DIR)
        if (!dcimDir.exists()) {
            dcimDir.mkdir()
        }
        //掃描全部檔案
        allFileList = dcimDir.listFiles()

        val layout = findViewById<ConstraintLayout>(R.id.constrain)
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.fillAfter = true
//        animation.duration = 3500
        animation.duration = 1000
        layout.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                println("動畫開始")
            }

            override fun onAnimationEnd(animation: Animation) {
                println("動畫結束")
                if (permissionManager.isCameraPermissionGranted() &&
                    permissionManager.isWriteExternalStoragePermissionGranted() &&
                    permissionManager.isReadExternalStoragePermissionGranted()
                ) {
                    mActivity.startActivity(intent)
                    mActivity.finish()
                } else {
                    // 如果沒有權限，您可以向使用者要求該權限
                    ActivityCompat.requestPermissions(
                        mActivity, *arrayOf(
                            WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE,
                            CAMERA
                        ), PERMISSION_REQUEST_CODE
                    );
                }

            }

            override fun onAnimationRepeat(animation: Animation) {
                println("動畫重覆執行")
            }
        })


//        imageMusicCover = findViewById(R.id.imageMusicCover)
//        //imageView 設定動畫元件(透明度調整)
//        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.start)
//        animation.isFillEnabled = true
//        animation.fillAfter = true
//        animation.setAnimationListener(this)
//        imageMusicCover.setAnimation(animation)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // 判斷權限是否都開啟
                if ((grantResults.isNotEmpty() && grantResults.all { it == 0 })
                ) {
                    // 有權限
                    mActivity.startActivity(intent)
                    mActivity.finish()
                } else {
                    // 無權限
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("權限未開啟")
                        .setMessage("請先前往[設定]>[應用程式]開啟權限")
                        .setPositiveButton(
                            "開啟設定",
                            DialogInterface.OnClickListener { _, _ ->
                                isSetting = true
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            })
                        .setNegativeButton(
                            "取消",
                            DialogInterface.OnClickListener { _, _ ->
                                finish()
                            })
                        .show()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 如果從設定出來才會啟動
        while (isSetting) {
            if (permissionManager.isCameraPermissionGranted() &&
                permissionManager.isWriteExternalStoragePermissionGranted() &&
                permissionManager.isReadExternalStoragePermissionGranted()
            ) {
                // 如果從設定出來後有成功開啟權限則重啟程式
                val intent = Intent(this, CoverActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                // 如果進入設定完還沒開啟全線直接退出程式
                finish()
            }
            isSetting = false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}