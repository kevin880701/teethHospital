package com.lhr.teethHospital.util.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getSystemService
import com.lhr.teethHospital.R
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.CAMERA_INTENT_FILTER
import com.lhr.teethHospital.model.Model.Companion.RECORD_DATE
import com.lhr.teethHospital.ui.camera.CameraActivity
import com.lhr.teethHospital.ui.editPatientInformation.EditPatientInformationViewModel
import java.text.SimpleDateFormat
import java.util.Date

class SaveRecordDialog(cameraActivity: CameraActivity) : Dialog(cameraActivity) {

    init {
        setContentView(R.layout.dialog_save_record)
        // 添加其他对话框配置

        val buttonOk = findViewById<Button>(R.id.buttonOk)
        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        val editRecordName = findViewById<EditText>(R.id.editRecordName)
        val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
        val time: String = recordDate.format(Date())
        editRecordName.setText(time) //預設紀錄名稱
        editRecordName.selectAll() // 全選EditText內容
        editRecordName.requestFocus() // 請求焦點
        buttonOk.setOnClickListener {

            dismiss()
            val intent = Intent(CAMERA_INTENT_FILTER)
            intent.putExtra(RECORD_DATE, editRecordName.text.toString())
            cameraActivity.sendBroadcast(intent)
        }

        buttonCancel.setOnClickListener {
            // 处理 Cancel 按钮点击事件
            dismiss()
        }
    }
}