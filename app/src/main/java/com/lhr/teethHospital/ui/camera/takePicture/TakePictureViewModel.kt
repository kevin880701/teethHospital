package com.lhr.teethHospital.ui.camera.takePicture

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.media.Image
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.lhr.teethHospital.model.Model.Companion.CAMERA_INTENT_FILTER
import com.lhr.teethHospital.model.Model.Companion.DETECT_PERCENT
import com.lhr.teethHospital.model.Model.Companion.DETECT_PICTURE
import com.lhr.teethHospital.model.Model.Companion.ORIGINAL_PICTURE
import com.lhr.teethHospital.ui.base.BaseViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TakePictureViewModel(context: Context) : BaseViewModel(context) {

    fun imageSaver(image: Image, //拍攝的照片
                   file: File, //儲存位址
                   takePictureActivity: TakePictureActivity){
        // 先儲存原始照片於file（檔案名稱 = RESULT_FILE_NAME）
        var completePictureFile = file
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(completePictureFile).apply {
                write(bytes)
            }
        } catch (e: IOException) {
            Log.e("ImageSaver", e.toString())
        } finally {
            image.close()
            output?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    Log.e("ImageSaver", e.toString())
                }
            }
        }
        // 讀取照片
        val bytes2 = completePictureFile.readBytes()
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes2.size)

        //轉90度 自拍轉270度
        var vMatrix = Matrix()
        if (takePictureActivity.currentCameraId == CameraCharacteristics.LENS_FACING_FRONT){
            vMatrix.setRotate(90f)
        }else if(takePictureActivity.currentCameraId == CameraCharacteristics.LENS_FACING_BACK){
            vMatrix.setRotate(270f)
            vMatrix.postScale(-1f, 1f)
        }

        // 轉完寫回Bitmap
        var bitmap2 = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width // 寬度
            , bitmap.height // 高度
            , vMatrix, true
        )
        //要被抓取的範圍
        var mask = getBitmapFromView(takePictureActivity.binding.imageViewBlack)
        val scaleWidth = (mask.height.toFloat() / bitmap.width.toFloat())
        val scaleHeight = (mask.width.toFloat() / bitmap.height.toFloat())
        vMatrix = Matrix()
        vMatrix.postScale(scaleHeight, scaleWidth)// 使用后乘

        var vB3 = Bitmap.createBitmap(
            bitmap2, 0, 0, bitmap2.width // 寬度
            , bitmap2.height // 高度
            , vMatrix, true
        )

        //提取嘴巴
        val width = mask.width
        val height = mask.height
        val originalBitmap = IntArray(width * height)
        val maskPixels = IntArray(width * height)
        val convPixels = IntArray(width * height)
        mask.getPixels(maskPixels, 0, width, 0, 0, width, height)
        vB3.getPixels(originalBitmap, 0, width, 0, 0, width, height)
        //取材切高寬
        var widthMin = 9999
        var widthMax = 0
        var heightMin = 9999
        var heightMax = 0
        for (i in 0 until width) {
            for (j in 0 until height) {
                val maskColor = mask.getPixel(i, j)
                val maskRed = Color.red(maskColor)
                val maskGreen = Color.green(maskColor)
                val maskBlue = Color.blue(maskColor)
                if (maskRed == 50 && maskGreen == 50 && maskBlue == 50) {
                    if (i < widthMin) {
                        widthMin = i
                    } else if (i > widthMax) {
                        widthMax = i
                    }
                    if (j < heightMin) {
                        heightMin = j
                    } else if (j > heightMax) {
                        heightMax = j
                    }
                }
            }
        }

        //遮罩裁切
        for (i in maskPixels.indices) {
            var clr = maskPixels[i]
            val maskRed: Int = Color.red(clr)
            val maskGreen: Int = Color.green(clr)
            val maskBlue: Int = Color.blue(clr)

            var clr2 = originalBitmap[i]
            val RR: Int = clr2 and 0xff0000 shr 16
            val GG: Int = clr2 and 0x00ff00 shr 8
            val BB: Int = clr2 and 0x0000ff shr 0

            val color: Int = Color.rgb(RR, GG, BB)

            if (maskRed == 50 && maskGreen == 50 && maskBlue == 50) {
                convPixels[i] = color
            } else {
                convPixels[i] = Color.TRANSPARENT
            }
        }
        var convBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        convBitmap.setPixels(convPixels, 0, width, 0, 0, width, height)
        //裁切
        var mask2 = getBitmapFromView(takePictureActivity.binding.imageViewLine)
        convBitmap = Bitmap.createBitmap(convBitmap, widthMin, heightMin, widthMax - widthMin, heightMax - heightMin)
        var (detectBitmap, percent) = getDetectPicture(convBitmap,mask2)

        val intent = Intent(CAMERA_INTENT_FILTER)
//        intent.putExtra(ORIGINAL_PICTURE, arrayListOf(convBitmap, detectBitmap))

        // Bitmap需顯轉換成數組在傳輸，不然太大
        var originalStream = ByteArrayOutputStream()
        convBitmap.compress(Bitmap.CompressFormat.PNG, 100, originalStream)
        var originalByteArray = originalStream.toByteArray()
        intent.putExtra(ORIGINAL_PICTURE, originalByteArray)
        // Bitmap需顯轉換成數組在傳輸，不然太大
        var detectStream = ByteArrayOutputStream()
        detectBitmap.compress(Bitmap.CompressFormat.PNG, 100, detectStream)
        var detectByteArray = detectStream.toByteArray()
        intent.putExtra(DETECT_PICTURE, detectByteArray)
        intent.putExtra(DETECT_PERCENT, percent)
        takePictureActivity.sendBroadcast(intent)

        if (percent>0.2){
//                    CameraActivity.cameraActivity.beforeDetectFragment.imageLight.visibility = View.VISIBLE
//            currentFragment.binding.imageLight.setImageDrawable(
//                ContextCompat.getDrawable(
//                    CameraActivity.cameraActivity, R.drawable.red_light))
        }else{
//                    CameraActivity.cameraActivity.beforeDetectFragment.imageLight.visibility = View.VISIBLE
//            currentFragment.binding.imageLight.setImageDrawable(
//                ContextCompat.getDrawable(
//                    CameraActivity.cameraActivity, R.drawable.green_light))
        }
//        isSetPicture = true
        takePictureActivity.finish()
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun getDetectPicture(bitmap: Bitmap, mask2: Bitmap): Pair<Bitmap,Float> {
        val width = bitmap.width
        val height = bitmap.height
        var count = 0.0F

        // 保存所有的像素的数组，图片宽×高
        val pixels = IntArray(width * height)
        val convPixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val clr = pixels[i]
            val R: Int = clr and 0xff0000 shr 16
            val G: Int = clr and 0x00ff00 shr 8
            val B: Int = clr and 0x0000ff shr 0

            if (clr == Color.TRANSPARENT) {
                convPixels[i] = Color.TRANSPARENT
//                count--
            } else if ((R in 94..227 && G in 32..141 && B in 47..191)) {
                convPixels[i] = Color.rgb(232, 119, 175)
                count++
            } else {
                convPixels[i] = Color.rgb(255, 255, 255)
            }
            if ((R in 109..187 && G in 50..130 && B in 57..148)) {
                convPixels[i] = Color.rgb(255, 255, 255)
                count--
            }
        }
        var mask2 = mask2
        val maskPixels = IntArray(width * height)
        mask2.getPixels(maskPixels, 0, width, 0, 0, width, height)


        val convBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        convBitmap.setPixels(convPixels, 0, width, 0, 0, width, height)

        var percent = (count)/(width * height)
        return Pair(convBitmap, percent)
    }
}