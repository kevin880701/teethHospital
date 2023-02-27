/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.lhr.teethHospital.util.Cover

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.Model.Model.Companion.DATABASES_PATH
import com.lhr.teethHospital.Model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.Model.Model.Companion.allFileList
import com.lhr.teethHospital.R
import com.lhr.teethHospital.util.Main.MainActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import java.io.File


@RuntimePermissions
class CoverActivity : AppCompatActivity() {
    lateinit var mActivity: Activity
    lateinit var presenter: CoverPresenter
    lateinit var imageMusicCover: ImageView
    lateinit var constrain: ConstraintLayout

    @NeedsPermission(
        *arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    )
    fun getMulti() {
    }

    @OnShowRationale(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA

    )
    fun showRationale(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setMessage("使用此功能需要WRITE_EXTERNAL_STORAGE和RECORD_AUDIO权限，下一步将继续请求权限")
            .setPositiveButton("下一步", DialogInterface.OnClickListener { dialog, which ->
                request.proceed() //继续执行请求
            }).setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                request.cancel() //取消执行请求
            })
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        setContentView(R.layout.activity_cover)
        supportActionBar!!.hide()

//        constrain = findViewById(R.id.constrain)
//        constrain.setBackgroundColor(R.drawable.cover_back)

        getMultiWithPermissionCheck()

        mActivity = this

        // 權限
//        presenter.notificationPermissions()

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
        animation.duration = 100
        layout.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                println("動畫開始")
            }

            override fun onAnimationEnd(animation: Animation) {
                println("動畫結束")

                mActivity.startActivity(intent)
                mActivity.finish()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}