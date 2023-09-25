package com.lhr.teethHospital.ui.detectResult

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.model.Model.Companion.detectPictureFileName
import com.lhr.teethHospital.model.Model.Companion.originalPictureFileName
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.model.Model.Companion.UPDATE_PATIENT_RECORD
import com.lhr.teethHospital.databinding.ActivityDetectResultBinding
import com.lhr.teethHospital.model.Model
import java.io.File
import java.text.DecimalFormat

class DetectResultActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var viewModel: DetectResultViewModel
    lateinit var binding: ActivityDetectResultBinding
    lateinit var recordEntity: RecordEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detect_result)
        viewModel = ViewModelProvider(
            this,
            DetectResultViewModelFactory(this.application)
        )[DetectResultViewModel::class.java]
        binding.viewModel = viewModel
        
        recordEntity = intent.getSerializableExtra("recordEntity") as RecordEntity
        // 使用正則表達式匹配同時包含 hospitalName 和 number 的字串
        val pattern = Regex("(${Regex.escape(recordEntity.hospitalName)}.*?${Regex.escape(recordEntity.number)})")
        // 使用 replace 將匹配的字串替換為空字串
        binding.textTitle.text = pattern.replace(recordEntity.fileName, "")
        println(recordEntity.detectPercent.toFloat())


        // 如果小於等於0代表沒有拍攝照片(percent預設-1.0)
        if(recordEntity.detectPercent.toFloat() >=0){
            val originalBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + originalPictureFileName)
            val originalBitmap: Bitmap =
                BitmapFactory.decodeStream(contentResolver.openInputStream(originalBitmapFile.toUri()))
            binding.imageOriginal.setImageBitmap(originalBitmap)
            val afterBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + detectPictureFileName)
            val afterBitmap: Bitmap =
                BitmapFactory.decodeStream(contentResolver.openInputStream(afterBitmapFile.toUri()))
            binding.imageDetect.setImageBitmap(afterBitmap)

            if (recordEntity.detectPercent.toFloat() > 0.2) {
//                imageLight.visibility = View.VISIBLE
//                binding.imageLight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.red_light))
            } else {
//                imageLight.visibility = View.VISIBLE
//                binding.imageLight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.green_light))
            }

            val df = DecimalFormat("00%")

            binding.textPercent.text = "殘留量：" + df.format(recordEntity.detectPercent.toFloat())
        }

        binding.imageBack.setOnClickListener(this)
        binding.imageDelete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                finish()
            }
            R.id.imageDelete -> {
                viewModel.deleteDirectory(TEETH_DIR + recordEntity.fileName + "/", recordEntity, this)

                val intent = Intent(UPDATE_PATIENT_RECORD)
                sendBroadcast(intent)
                //更新歷史資訊資料
//                val dcimDir = File(PICTURE_DIR)
//                allFileList = dcimDir.listFiles()
//                historyActivity.historyAdapter.notifyDataSetChanged()

                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}