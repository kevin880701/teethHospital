/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.lhr.teethHospital.ui.cover

import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.android.notesk.SQLite.SqlModel.Companion.DB_NAME
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.model.Model.Companion.DATABASES_PATH
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.model.Model.Companion.allFileList
import com.lhr.teethHospital.permission.PermissionManager
import com.lhr.teethHospital.permission.PermissionManager.Companion.CAMERA
import com.lhr.teethHospital.permission.PermissionManager.Companion.READ_EXTERNAL_STORAGE
import com.lhr.teethHospital.permission.PermissionManager.Companion.WRITE_EXTERNAL_STORAGE
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.ActivityCoverBinding
import com.lhr.teethHospital.net.NetManager
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.ui.login.LoginActivity
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel
import java.io.File


class CoverActivity : BaseActivity() {

    lateinit var viewModel: CoverViewModel
    lateinit var binding: ActivityCoverBinding
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cover)
        viewModel = ViewModelProvider(
            this,
            CoverViewModelFactory(this.application)
        )[CoverViewModel::class.java]
        binding.viewModel = viewModel

        // 創建Model
        Model
        APP_FILES_PATH = getExternalFilesDir(null)!!.absolutePath
        DATABASES_PATH = this.getCacheDir().parent + "/databases"
        TEETH_DIR = getExternalFilesDir(null)!!.absolutePath.toString() + "/teeth/"
        // 在DCIM中創建存圖的資料夾
        viewModel.creeateFolder()

        //取資料成功觸發
        viewModel.isDataGet.observe(this) { newId ->
            if(newId) {

                //先檢查權限
                viewModel.checkPermission(this)
//                val intent = Intent(this@CoverActivity, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }

        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.fillAfter = true
        animation.duration = 1000
        binding.constrain.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                println("動畫開始")
            }

            override fun onAnimationEnd(animation: Animation) {
                println("動畫結束")

                // 從Room取患者資料，有取到就跳轉頁面
                viewModel.getHospitalInfo()

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
                    // 有權限並重新啟動Cover頁面
                    val intent = Intent(this, CoverActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
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