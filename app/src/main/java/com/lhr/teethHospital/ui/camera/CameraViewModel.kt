package com.lhr.teethHospital.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.model.Model.Companion.PERCENT_RECORD
import com.lhr.teethHospital.model.Model.Companion.detectPictureFileName
import com.lhr.teethHospital.model.Model.Companion.originalPictureFileName
import com.lhr.teethHospital.model.Model.Companion.rangePictureFileName
import com.lhr.teethHospital.net.NetManager
import com.lhr.teethHospital.net.response.toUploadResponse
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseViewModel
import com.lhr.teethHospital.ui.base.LoadState
import com.lhr.teethHospital.util.loadImageAsBitmap
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date


class CameraViewModel(context: Context, var personalManagerRepository: PersonalManagerRepository) :
    BaseViewModel(context) {

    var originalImage = MutableLiveData<Bitmap?>()
    var rangeImage = MutableLiveData<Bitmap?>()
    var detectImage = MutableLiveData<Bitmap?>()
    var detectPercent = MutableLiveData<String>()
    var netManager = NetManager(context)
    fun cleanImage() {

        originalImage.postValue(null)
        rangeImage.postValue(null)
        detectImage.postValue(null)
        detectPercent.postValue("")
    }

//    fun choosePhotoAlbum(cameraActivity: CameraActivity, imageUri: Uri) {
//        val originalParcelFileDescriptor = cameraActivity.contentResolver.openFileDescriptor(imageUri, "r")
//        val originalImage = BitmapFactory.decodeFileDescriptor(originalParcelFileDescriptor!!.fileDescriptor)
//        cameraActivity.binding.imageOriginal.setImageBitmap(originalImage)
//        val (afterImage, percent) = getDetectPicture(originalImage)
//        cameraActivity.binding.imageDetect.setImageBitmap(afterImage)
//        if (percent > 0.2) {
////            cameraActivity.imageLight.visibility = View.VISIBLE
////            cameraActivity.binding.imageLight.setImageDrawable(ContextCompat.getDrawable(CameraActivity.cameraActivity, R.drawable.red_light))
//        } else {
////            cameraActivity.imageLight.visibility = View.VISIBLE
////            cameraActivity.binding.imageLight.setImageDrawable(ContextCompat.getDrawable(CameraActivity.cameraActivity, R.drawable.green_light))
//        }
//        val df = DecimalFormat("00%")
//        cameraActivity.binding.textPercent.text = "殘留量：" + df.format(percent)
//        cameraActivity.percent = percent
//    }
//
//
//    fun setTakePicture(imageView: ImageView, bitmap: Bitmap) {
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                imageView.setImageBitmap(bitmap)
//            }
//        }
//    }

    @SuppressLint("CheckResult")
    fun uploadImage(image: RequestBody) {
        netManager.apiService.uploadImage(image)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        val message = responseData.message
                        val data = responseData.data
                        println("message：$message")
                        println("data：${data}")
                        var uploadResponse = data.toUploadResponse()

                        GlobalScope.launch(Dispatchers.IO) {
                            rangeImage.postValue(
                                loadImageAsBitmap(
                                    "${NetManager(context).baseUrl}getImage?image_path=${uploadResponse.teethRangePath}",
                                    context
                                )
                            )
                            detectImage.postValue(
                                loadImageAsBitmap(
                                    "${NetManager(context).baseUrl}getImage?image_path=${uploadResponse.teethRangeDetectPath}",
                                    context
                                )
                            )
                        }

                        detectPercent.postValue(message)
                        println("teethRangePath：${uploadResponse.teethRangePath}")
                        println("teethRangePath：${uploadResponse.teethRangeDetectPath}")
                        hideLoading()
                        showToast(message)
                    }
                } else {
                    showToast("錯誤")
                    hideLoading()
                    // 处理请求失败
                }
            }, { error ->
                showToast("URL錯誤")
                println("Error：${error}")
                hideLoading()
                // 处理网络请求错误
            })
    }

    //
//    fun getImage(imagePath: String, imageview:ImageView){
//        NetManager.apiService.getImage(imagePath)
//            .subscribeOn(Schedulers.io()) // 指定在 IO 线程执行网络请求
//            .observeOn(AndroidSchedulers.mainThread()) // 指定在主线程处理响应结果
//            .subscribe({ response ->
//                // 处理成功响应
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    // 对返回的数据进行处理
//                } else {
//                    // 处理服务器返回的错误
//                }
//            }, { error ->
//                // 处理发生的错误
//            })
//    }
//
    fun saveImage(hospitalEntity: HospitalEntity, folderName: String) {
        val recordDate = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
        var savePath = Model.TEETH_DIR + hospitalEntity.hospitalName + hospitalEntity.number + folderName + "/"
        val file = File(savePath)
        if (!file.exists()) {
            file.mkdir()
        }

        // 如果有設置照片
        convertViewToBitmap(originalImage.value, savePath, originalPictureFileName)
        convertViewToBitmap(rangeImage.value, savePath, rangePictureFileName)
        convertViewToBitmap(detectImage.value, savePath, detectPictureFileName)
        // 將偵測出的牙菌斑占比寫入txt
        val fileName = savePath + PERCENT_RECORD
        val myfile = File(fileName)
        myfile.bufferedWriter().use { out ->
            out.write(detectPercent.value.toString() + "\n")
            out.write(detectPercent.value.toString())
        }

        var recordEntity = RecordEntity()
        recordEntity.hospitalName = hospitalEntity.hospitalName
        recordEntity.number = hospitalEntity.number
        recordEntity.gender = hospitalEntity.gender
        recordEntity.birthday = hospitalEntity.birthday
        recordEntity.fileName = hospitalEntity.hospitalName + hospitalEntity.number + folderName
        recordEntity.recordDate = recordDate.format(Date())
        recordEntity.detectPercent = detectPercent.value.toString()
        SqlDatabase.getInstance().getRecordDao().insert(recordEntity)
        personalManagerRepository.getAllMemberRecord()
    }

    fun convertViewToBitmap(bitmap: Bitmap?, savePath: String, fileName: String) {
//        val drawable = imageView.drawable as BitmapDrawable
//        val bitmap = drawable.bitmap
        if (bitmap != null) {
            var outStream: FileOutputStream?
            val outFile = File(savePath, fileName)
            outStream = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        }
    }
}