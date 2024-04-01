package com.lhr.teethHospital.ui.camera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.activity.viewModels
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.ActivityCameraBinding
import com.lhr.teethHospital.model.Model.Companion.CAMERA_INTENT_FILTER
import com.lhr.teethHospital.model.Model.Companion.DETECT_PERCENT
import com.lhr.teethHospital.model.Model.Companion.DETECT_PICTURE
import com.lhr.teethHospital.model.Model.Companion.ORIGINAL_PICTURE
import com.lhr.teethHospital.model.Model.Companion.RECORD_DATE
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.model.Model.Companion.UPDATE_PATIENT_RECORD
import com.lhr.teethHospital.model.Model.Companion.isSetPicture
import com.lhr.teethHospital.net.NetManager
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.util.createImageFile
import com.lhr.teethHospital.util.dialog.SaveRecordDialog
import com.lhr.teethHospital.util.popupWindow.ChooseImagePopupWindow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream


class CameraActivity : BaseActivity(), View.OnClickListener {
    companion object {
        lateinit var cameraActivity: CameraActivity
        lateinit var takePicture: ActivityResultLauncher<Void>
    }

    private val viewModel: CameraViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }
    private var _binding: ActivityCameraBinding? = null
    val binding get() = _binding!!
    lateinit var messageReceiver: BroadcastReceiver
    lateinit var hospitalEntity: HospitalEntity
    var percent = -1.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hospitalEntity = intent.getSerializableExtra(ROOT) as HospitalEntity

        takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            if (result != null) {
                val imageFile = createImageFile(this) // 创建一个临时文件用于保存图片
                val outputStream = FileOutputStream(imageFile)
                result.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // 将 Bitmap 保存到文件
                outputStream.close()
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull()) // 将文件转换为 RequestBody
                val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val params = HashMap<String, RequestBody>()
                params["file"] = requestBody

                NetManager().uploadImage(requestBody)

                binding.imageOriginal.setImageBitmap(result)
            }
        }
        cameraActivity = this

        // 註冊 BroadcastReceiver
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == CAMERA_INTENT_FILTER) {
                    if (intent.hasExtra(ORIGINAL_PICTURE) && intent.hasExtra(DETECT_PICTURE)) {
                        var originalBitmapByteArray = intent.getByteArrayExtra(ORIGINAL_PICTURE)
                        var originalBitmap =
                            BitmapFactory.decodeByteArray(originalBitmapByteArray, 0, originalBitmapByteArray!!.size)
                        viewModel.setTakePicture(binding.imageOriginal, originalBitmap!!)
                        var detectBitmapByteArray = intent.getByteArrayExtra(DETECT_PICTURE)
                        var detectBitmap =
                            BitmapFactory.decodeByteArray(detectBitmapByteArray, 0, detectBitmapByteArray!!.size)
                        viewModel.setTakePicture(binding.imageDetect, detectBitmap!!)
                        percent = intent.getFloatExtra(DETECT_PERCENT, 0.0F)
                        isSetPicture = true
                    }
                    if (intent.hasExtra(RECORD_DATE)) {
                        val recordDate = intent.getStringExtra(RECORD_DATE)
                        cameraActivity.viewModel.saveRecord(hospitalEntity, this@CameraActivity, recordDate!!)
                        // 更新患者紀錄Adapter
                        sendBroadcast(
                            Intent(UPDATE_PATIENT_RECORD).putExtra("action", UPDATE_PATIENT_RECORD)
                        )
                        finish()
                    }
                }
            }
        }
        val intentFilter = IntentFilter(CAMERA_INTENT_FILTER)
        this.registerReceiver(messageReceiver, intentFilter)

        binding.buttonChoosePicture.setOnClickListener(this)
        binding.buttonSaveRecord.setOnClickListener(this)
        binding.buttonCleanImage.setOnClickListener(this)
        binding.titleBar.binding.imageBack.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                val intent = Intent(UPDATE_PATIENT_RECORD)
                sendBroadcast(intent)
                finish()
            }

            R.id.buttonChoosePicture -> {
                val choose = ChooseImagePopupWindow(this)
                val view: View = LayoutInflater.from(this).inflate(
                    R.layout.popup_window_choose_image,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
            }
            // 圖片儲存
            R.id.buttonSaveRecord -> {
                if (!(isSetPicture)) {
                    Toast.makeText(this, "圖片未選擇", Toast.LENGTH_SHORT).show()
                } else {
                    val customDialog = SaveRecordDialog(this)
                    customDialog.show()
                }
            }

            R.id.buttonCleanImage -> {
                viewModel.cleanImage(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消註冊 BroadcastReceiver
        unregisterReceiver(messageReceiver)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean { //捕捉返回鍵
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val myData: Intent? = result.data
            if (myData != null) {
                // 選擇圖片後變更圖片
                viewModel.choosePhotoAlbum(this, myData.data!!)
                isSetPicture = true
            }
        }
    }
}