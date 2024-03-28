package com.lhr.teethHospital.ui.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.detectPictureFileName
import com.lhr.teethHospital.model.Model.Companion.isSetPicture
import com.lhr.teethHospital.model.Model.Companion.originalPictureFileName
import com.lhr.teethHospital.model.Model.Companion.PERCENT_RECORD
import com.lhr.teethHospital.model.Model.Companion.allFileList
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.room.SqlDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    companion object{

    }

    fun cleanImage(cameraActivity: CameraActivity){
        cameraActivity.binding.imageOriginal.setImageDrawable(null)
        cameraActivity.binding.imageDetect.setImageDrawable(null)
        cameraActivity.binding.textPercent.text = ""
        cameraActivity.percent = 0.0F
        isSetPicture = false
    }

    fun choosePhotoAlbum(cameraActivity: CameraActivity, imageUri: Uri) {
        val originalParcelFileDescriptor = cameraActivity.contentResolver.openFileDescriptor(imageUri, "r")
        val originalImage = BitmapFactory.decodeFileDescriptor(originalParcelFileDescriptor!!.fileDescriptor)
        cameraActivity.binding.imageOriginal.setImageBitmap(originalImage)
        val (afterImage, percent) = getDetectPicture(originalImage)
        cameraActivity.binding.imageDetect.setImageBitmap(afterImage)
        if (percent>0.2){
//            cameraActivity.imageLight.visibility = View.VISIBLE
//            cameraActivity.binding.imageLight.setImageDrawable(ContextCompat.getDrawable(CameraActivity.cameraActivity, R.drawable.red_light))
        }else{
//            cameraActivity.imageLight.visibility = View.VISIBLE
//            cameraActivity.binding.imageLight.setImageDrawable(ContextCompat.getDrawable(CameraActivity.cameraActivity, R.drawable.green_light))
        }
        val df = DecimalFormat("00%")
        cameraActivity.binding.textPercent.text = "殘留量：" + df.format(percent)
        cameraActivity.percent = percent
    }


    fun setTakePicture(imageView: ImageView, bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

//    fun saveRecord(hospitalEntity: HospitalEntity, cameraActivity: CameraActivity, dataBase: SqlDatabase){
//        val recordDate = SimpleDateFormat("-yyyy-MM-dd-hh-mm-ss")
//        val time: String = recordDate.format(Date())
//        var savePath = Model.TEETH_DIR + hospitalEntity.hospitalName + hospitalEntity.number + time + "/"
//        val file = File(savePath)
//        if (!file.exists()) {
//            file.mkdir()
//        }
//
//        // 如果有設置照片
//        if(isSetPicture){
//            convertViewToBitmap(cameraActivity.binding.imageOriginal,savePath,originalPictureFileName)
//            convertViewToBitmap(cameraActivity.binding.imageDetect,savePath, detectPictureFileName)
//        }
//        // 將偵測出的牙菌斑占比寫入txt
//        val fileName = savePath + PERCENT_RECORD
//        val myfile = File(fileName)
//        myfile.bufferedWriter().use { out ->
//            out.write((cameraActivity.percent).toString() + "\n")
//            out.write((cameraActivity.percent).toString())
//        }
//
//        //更新歷史資訊資料
//        val dcimDir = File(Model.TEETH_DIR)
//        allFileList = dcimDir.listFiles()
//
//        runBlocking {     // 阻塞主執行緒
//            launch(Dispatchers.IO) {
//                var recordEntity = RecordEntity()
//                recordEntity.hospitalName = hospitalEntity.hospitalName
//                recordEntity.number = hospitalEntity.number
//                recordEntity.gender = hospitalEntity.gender
//                recordEntity.birthday = hospitalEntity.birthday
//                recordEntity.fileName = hospitalEntity.hospitalName + hospitalEntity.number + time
//                recordEntity.recordDate = recordDate.format(Date())
//                recordEntity.detectPercent = cameraActivity.percent.toString()
//                dataBase.getRecordDao().insert(recordEntity)
//            }
//        }
//    }

    fun saveRecord(hospitalEntity: HospitalEntity, cameraActivity: CameraActivity, folderName: String){
        val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
        var savePath = Model.TEETH_DIR + hospitalEntity.hospitalName + hospitalEntity.number + folderName + "/"
        val file = File(savePath)
        if (!file.exists()) {
            file.mkdir()
        }

        // 如果有設置照片
        if(isSetPicture){
            convertViewToBitmap(cameraActivity.binding.imageOriginal,savePath,originalPictureFileName)
            convertViewToBitmap(cameraActivity.binding.imageDetect,savePath, detectPictureFileName)
        }
        // 將偵測出的牙菌斑占比寫入txt
        val fileName = savePath + PERCENT_RECORD
        val myfile = File(fileName)
        myfile.bufferedWriter().use { out ->
            out.write((cameraActivity.percent).toString() + "\n")
            out.write((cameraActivity.percent).toString())
        }

        //更新歷史資訊資料
        val dcimDir = File(Model.TEETH_DIR)
        allFileList = dcimDir.listFiles()

        runBlocking {     // 阻塞主執行緒
            launch(Dispatchers.IO) {
                var recordEntity = RecordEntity()
                recordEntity.hospitalName = hospitalEntity.hospitalName
                recordEntity.number = hospitalEntity.number
                recordEntity.gender = hospitalEntity.gender
                recordEntity.birthday = hospitalEntity.birthday
                recordEntity.fileName = hospitalEntity.hospitalName + hospitalEntity.number + folderName
                recordEntity.recordDate = recordDate.format(Date())
                recordEntity.detectPercent = cameraActivity.percent.toString()
                SqlDatabase.getInstance().getRecordDao().insert(recordEntity)
            }
        }
    }

    fun getDetectPicture(bitmap: Bitmap):Pair<Bitmap,Float>  {
        val width = bitmap.width
        val height = bitmap.height
        var count = 0.0F

        // 保存所有的像素的數組，圖片寬×高
        val pixels = IntArray(width * height)
        val convPixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val clr = pixels[i]
            val R: Int = Color.red(clr)
            val G: Int = Color.green(clr)
            val B: Int = Color.blue(clr)
            val color: Int = Color.rgb(R, G, B)

            if (clr == Color.TRANSPARENT) {
                convPixels[i] = Color.TRANSPARENT
//                count--
            } else if ((R in 94..227 && G in 32..141 && B in 47..191)) {
                convPixels[i] = Color.rgb(232, 119, 175)
                count++
            } else {
//                convPixels[i] = Color.WHITE
                convPixels[i] = Color.rgb(255, 255, 255)
            }
            if ((R in 109..187 && G in 50..130 && B in 57..148)) {
                convPixels[i] = Color.rgb(255, 255, 255)
                count--
            }
        }

        val convBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        convBitmap.setPixels(convPixels, 0, width, 0, 0, width, height)
        var percent = (count)/(width * height)
        return Pair(convBitmap, percent)
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