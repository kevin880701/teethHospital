package com.lhr.teethHospital.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.CLEAN_AFTER_EXIST
import com.lhr.teethHospital.Model.Model.Companion.CLEAN_BEFORE_EXIST
import com.lhr.teethHospital.Model.Model.Companion.CleanRecordFilter
import com.lhr.teethHospital.Model.Model.Companion.PERCENT_RECORD
import com.lhr.teethHospital.Model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.Model.Model.Companion.UPDATE_CLEAN_RECORD
import com.lhr.teethHospital.Model.Model.Companion.allFileList
import com.lhr.teethHospital.Room.HospitalEntity
import com.lhr.teethHospital.Room.RecordEntity
import com.lhr.teethHospital.Room.SqlDatabase
import com.lhr.teethHospital.util.Camera.CameraActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraPresenter(cameraActivity: CameraActivity) {
    var cameraActivity = cameraActivity
    var dataBase = SqlDatabase(cameraActivity)
    var writePercentRecord = ""

    fun saveRecord(classEntity: HospitalEntity){
        val recordDate = SimpleDateFormat("-yyyy-MM-dd-hh-mm-ss")
//        val recordDate = SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss")
        val time: String = recordDate.format(Date())
        var savePath = TEETH_DIR + classEntity.hospitalName + classEntity.number + time + "/"
        val file = File(savePath)
        if (!file.exists()) {
            file.mkdir()
        }

        // 如果清潔前有照片
        if(CLEAN_BEFORE_EXIST){
            convertViewToBitmap(cameraActivity.beforeDetectFragment.imageOriginal,savePath, Model.CLEAN_BEFORE_ORIGINAL)
            convertViewToBitmap(cameraActivity.beforeDetectFragment.imageAfter,savePath, Model.CLEAN_BEFORE_DETECT)
        }
        // 如果清潔後有照片
        if(CLEAN_AFTER_EXIST){
            convertViewToBitmap(cameraActivity.afterDetectFragment.imageOriginal,savePath, Model.CLEAN_AFTER_ORIGINAL)
            convertViewToBitmap(cameraActivity.afterDetectFragment.imageAfter,savePath, Model.CLEAN_AFTER_DETECT)
        }
        // 將偵測出的牙菌斑占比寫入txt
        val fileName = savePath + PERCENT_RECORD
        val myfile = File(fileName)
        myfile.bufferedWriter().use { out ->
            out.write((cameraActivity.beforeDetectFragment.percent).toString() + "\n")
            out.write((cameraActivity.afterDetectFragment.percent).toString())
        }

        //更新歷史資訊資料
        val dcimDir = File(TEETH_DIR)
        allFileList = dcimDir.listFiles()

        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                var recordEntity = RecordEntity()
                recordEntity.hospitalName = classEntity.hospitalName
                recordEntity.number = classEntity.number
                recordEntity.gender = classEntity.gender
                recordEntity.birthday = classEntity.birthday
                recordEntity.fileName = classEntity.hospitalName + classEntity.number + time
                recordEntity.recordDate = recordDate.format(Date())
                recordEntity.beforePercent = cameraActivity.beforeDetectFragment.percent.toString()
                recordEntity.afterPercent = cameraActivity.afterDetectFragment.percent.toString()
                dataBase.getRecordDao().insert(recordEntity)
            }
        }
        cameraActivity.sendBroadcast(
            Intent(CleanRecordFilter).putExtra("action", UPDATE_CLEAN_RECORD)
        )
    }

    fun convertViewToBitmap(imageView: ImageView, savePath: String, fileName: String) {
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        var outStream: FileOutputStream?
        val outFile = File(savePath, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.flush()
        outStream.close()
    }

    fun cleanImage(){
        when (cameraActivity.viewPager.currentItem) {
            0 -> {
                cameraActivity.beforeDetectFragment.imageOriginal.setImageDrawable(null)
                cameraActivity.beforeDetectFragment.imageAfter.setImageDrawable(null)
                cameraActivity.beforeDetectFragment.imageLight.visibility = View.INVISIBLE
                cameraActivity.beforeDetectFragment.textPercent.text = ""
                CLEAN_BEFORE_EXIST = false
                cameraActivity.beforeDetectFragment.percent = 0.0F
            }
            1-> {
                cameraActivity.afterDetectFragment.imageOriginal.setImageDrawable(null)
                cameraActivity.afterDetectFragment.imageAfter.setImageDrawable(null)
                cameraActivity.afterDetectFragment.imageLight.visibility = View.INVISIBLE
                cameraActivity.afterDetectFragment.textPercent.text = ""
                CLEAN_AFTER_EXIST = false
                cameraActivity.afterDetectFragment.percent = 0.0F
            }
        }
    }
}