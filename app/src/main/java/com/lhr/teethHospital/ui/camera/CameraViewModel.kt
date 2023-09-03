package com.lhr.teethHospital.ui.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager2.widget.ViewPager2
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.CLEAN_AFTER_DETECT
import com.lhr.teethHospital.model.Model.Companion.CLEAN_AFTER_EXIST
import com.lhr.teethHospital.model.Model.Companion.CLEAN_AFTER_ORIGINAL
import com.lhr.teethHospital.model.Model.Companion.CLEAN_BEFORE_DETECT
import com.lhr.teethHospital.model.Model.Companion.CLEAN_BEFORE_EXIST
import com.lhr.teethHospital.model.Model.Companion.CLEAN_BEFORE_ORIGINAL
import com.lhr.teethHospital.model.Model.Companion.PERCENT_RECORD
import com.lhr.teethHospital.data.camera.CameraRepository
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.ui.camera.detect.DetectFragment
import com.lhr.teethHospital.viewPager.ViewPageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    companion object
    var cameraRepository = CameraRepository(application)

    fun cleanImage(pageAdapter: ViewPageAdapter, viewPager: ViewPager2){
        val currentFragment = pageAdapter.fragments[viewPager.currentItem] as DetectFragment
        currentFragment.binding.imageOriginal.setImageDrawable(null)
        currentFragment.binding.imageAfter.setImageDrawable(null)
        currentFragment.binding.textPercent.text = ""
        currentFragment.percent = 0.0F
        when (viewPager.currentItem) {
            0 -> {
                CLEAN_BEFORE_EXIST = false

            }
            1-> {
                CLEAN_AFTER_EXIST = false
            }
        }
    }
    fun saveRecord(hospitalEntity: HospitalEntity, fragments: ArrayList<Fragment>, dataBase: SqlDatabase){
        var fragments = fragments as ArrayList<DetectFragment>
        val recordDate = SimpleDateFormat("-yyyy-MM-dd-hh-mm-ss")
        val time: String = recordDate.format(Date())
        var savePath = Model.TEETH_DIR + hospitalEntity.hospitalName + hospitalEntity.number + time + "/"
        val file = File(savePath)
        if (!file.exists()) {
            file.mkdir()
        }

        // 如果清潔前有照片
        if(CLEAN_BEFORE_EXIST){
            convertViewToBitmap(fragments[0].binding.imageOriginal,savePath,CLEAN_BEFORE_ORIGINAL)
            convertViewToBitmap(fragments[0].binding.imageAfter,savePath, CLEAN_BEFORE_DETECT)
        }
        // 如果清潔後有照片
        if(CLEAN_AFTER_EXIST){
            convertViewToBitmap(fragments[1].binding.imageOriginal,savePath, CLEAN_AFTER_ORIGINAL)
            convertViewToBitmap(fragments[1].binding.imageAfter,savePath, CLEAN_AFTER_DETECT)
        }
        // 將偵測出的牙菌斑占比寫入txt
        val fileName = savePath + PERCENT_RECORD
        val myfile = File(fileName)
        myfile.bufferedWriter().use { out ->
            out.write((fragments[0].percent).toString() + "\n")
            out.write((fragments[1].percent).toString())
        }

        //更新歷史資訊資料
        val dcimDir = File(Model.TEETH_DIR)
        Model.allFileList = dcimDir.listFiles()

        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                var recordEntity = RecordEntity()
                recordEntity.hospitalName = hospitalEntity.hospitalName
                recordEntity.number = hospitalEntity.number
                recordEntity.gender = hospitalEntity.gender
                recordEntity.birthday = hospitalEntity.birthday
                recordEntity.fileName = hospitalEntity.hospitalName + hospitalEntity.number + time
                recordEntity.recordDate = recordDate.format(Date())
                recordEntity.beforePercent = fragments[0].percent.toString()
                recordEntity.afterPercent = fragments[1].percent.toString()
                dataBase.getRecordDao().insert(recordEntity)
            }
        }
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
}