package com.lhr.teethHospital.util.popupWindow

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import com.lhr.teethHospital.R
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.ui.camera.CameraActivity
import com.lhr.teethHospital.ui.camera.CameraActivity.Companion.takePicture
import com.lhr.teethHospital.ui.camera.takePicture.TakePictureActivity

class ChooseImagePopupWindow(var context: Context) : PopupWindow(), View.OnClickListener {
    var buttonTakePicture: Button
    var buttonChooseImage: Button

    init {
        var view = LayoutInflater.from(context).inflate(R.layout.popup_window_choose_image, null)
        buttonTakePicture = view.findViewById(R.id.buttonImportMore)
        buttonChooseImage = view.findViewById(R.id.buttonChoosePicture)
        // 外部可點擊
        this.isOutsideTouchable = true
        // mMenuView添加OnTouchListener監聽判斷獲取觸屏位置如果在選擇框外面則銷毀彈出框
        view.setOnTouchListener { v, event ->
            val height = view.findViewById<View>(R.id.popLayout).top
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }
        this.contentView = view
        // 窗口高和寬填滿
        this.height = RelativeLayout.LayoutParams.MATCH_PARENT
        this.width = RelativeLayout.LayoutParams.MATCH_PARENT
        // 設置彈出窗體可點擊
        this.isFocusable = true
        // 背景色
        val dw = ColorDrawable(-0x50000000)
        setBackgroundDrawable(dw)
        // 彈出窗体的動畫
        this.animationStyle = R.style.take_photo_anim

        buttonTakePicture.setOnClickListener(this)
        buttonChooseImage.setOnClickListener(this)
    }

    fun chooseImage() {
//        val pickIntent = Intent(
//            Intent.ACTION_PICK,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        )
//        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Model.IMAGE_URI)
//        val chooserIntent = Intent.createChooser(pickIntent, "Choose")
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(takePhotoIntent))
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
////            activityForResult.launch(chooserIntent)
//            Log.v("PPPPPPPPPPPP","Build.VERSION_CODES.Q")
//            mActivity.startForResult.launch(chooserIntent)
//        } else {
//            //support for older than android 11
//            Log.v("PPPPPPPPPPPP","support for older than android 11")
////            startActivityForResult(mActivity, chooserIntent, Model.IMAGE_REQUEST_CODE, null)
//        }
//        dismiss()
    }

    fun takePictureClick() {
        dismiss()
//        val takePictureIntent = Intent(mActivity, TakePictureActivity::class.java)
//        mActivity.startForResult.launch(takePictureIntent)

        takePicture.launch(null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonImportMore -> {
                takePictureClick()
            }R.id.buttonChoosePicture -> {
                chooseImage()
            }
        }
    }
}