package com.lhr.teethHospital.util.popupWindow

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.R
import com.lhr.teethHospital.ui.editPatientInformation.EditPatientInformationActivity
import com.lhr.teethHospital.ui.personalManager.PersonalManagerFragment

class ImportPopupWindow(mContext: Context, personalManagerFragment: PersonalManagerFragment) : PopupWindow(), View.OnClickListener {
    val mContext = mContext
    var view: View
    var buttonImportMore: Button
    var buttonImportSingle: Button
    var personalManagerFragment = personalManagerFragment

    init {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_window_import, null)
        buttonImportMore = view.findViewById(R.id.buttonImportMore)
        buttonImportSingle = view.findViewById(R.id.buttonChoosePicture)
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

        buttonImportMore.setOnClickListener(this)
        buttonImportSingle.setOnClickListener(this)
    }

    fun importMore() {
        val pickIntent = Intent(
            Intent.ACTION_GET_CONTENT
        )
        pickIntent.type = "*/*"
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Log.v("PPPPPPPPPPPP","Build.VERSION_CODES.Q")
            personalManagerFragment.importResult.launch(pickIntent)
        } else {
            //support for older than android 11
            Log.v("PPPPPPPPPPPP","support for older than android 11")
            ActivityCompat.startActivityForResult(personalManagerFragment.requireActivity(), pickIntent, Model.IMPORT_CSV, null)
        }
        dismiss()
    }

    fun importSingle() {
        val intent = Intent(personalManagerFragment.requireActivity(), EditPatientInformationActivity::class.java)
        personalManagerFragment.requireActivity().startActivity(intent)
        dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonImportMore -> {
                importMore()
            }R.id.buttonChoosePicture -> {
                importSingle()
            }
        }
    }
}