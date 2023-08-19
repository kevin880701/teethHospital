/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.lhr.teethHospital.util.Camera.TakePicture

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.*
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import androidx.core.content.ContextCompat
import com.example.android.camera2basic.ImageSaver
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.R
import com.lhr.teethHospital.util.Camera.CameraActivity
import com.lhr.teethHospital.util.Camera.CompareSizesByArea
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class TakePicturePresenter(takePictureActivity: TakePictureActivity) {
    var takePicture2Activity = takePictureActivity
    var textTureCapture = takePictureActivity.textTureCapture

    fun imageSaver(image: Image, file: File,takePictureActivity: TakePictureActivity){
//        var detectFile = File(mainActivity!!.getExternalFilesDir(null), Model.RESULT_FILE_NAME)
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())

        buffer.get(bytes)

        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file).apply {
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
        //轉90度
        val bytes2 = File(mainActivity.getExternalFilesDir(null), Model.PIC_FILE_NAME).readBytes()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes2.size)
        var mask = getBitmapFromView(takePictureActivity.imageViewBlack)
        var mask2 = getBitmapFromView(takePictureActivity.imageViewLine)

        var vMatrix = Matrix()
        if (takePictureActivity.currentCameraId == CameraCharacteristics.LENS_FACING_FRONT){
            vMatrix.setRotate(90f)
        }else if(takePictureActivity.currentCameraId == CameraCharacteristics.LENS_FACING_BACK){
            vMatrix.setRotate(270f)
            vMatrix.postScale(-1f, 1f)
        }

        var vB2 = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width // 寬度
            , bitmap.height // 高度
            , vMatrix, true
        )

        val scaleWidth = (mask.height.toFloat() / bitmap.width.toFloat())
        val scaleHeight = (mask.width.toFloat() / bitmap.height.toFloat())
        vMatrix = Matrix()
        vMatrix.postScale(scaleHeight, scaleWidth)// 使用后乘

        var vB3 = Bitmap.createBitmap(
            vB2, 0, 0, vB2.width // 寬度
            , vB2.height // 高度
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
        convBitmap = Bitmap.createBitmap(convBitmap, widthMin, heightMin, widthMax - widthMin, heightMax - heightMin)
//        var detectBitmap = getDetectPicture(convBitmap,mask2)
        var (detectBitmap, percent) = getDetectPicture(convBitmap,mask2)
        when (CameraActivity.cameraActivity.viewPager.currentItem) {
            0 -> {
                CameraActivity.cameraActivity.beforeDetectFragment.presenter.setTakePicture(CameraActivity.cameraActivity.beforeDetectFragment, convBitmap, detectBitmap)
                Model.CLEAN_BEFORE_EXIST = true
                if (percent>0.2){
                    CameraActivity.cameraActivity.beforeDetectFragment.imageLight.visibility = View.VISIBLE
                    CameraActivity.cameraActivity.beforeDetectFragment.imageLight.setImageDrawable(
                        ContextCompat.getDrawable(
                            CameraActivity.cameraActivity, R.drawable.red_light))
                }else{
                    CameraActivity.cameraActivity.beforeDetectFragment.imageLight.visibility = View.VISIBLE
                    CameraActivity.cameraActivity.beforeDetectFragment.imageLight.setImageDrawable(
                        ContextCompat.getDrawable(
                            CameraActivity.cameraActivity, R.drawable.green_light))
                }
                CameraActivity.cameraActivity.beforeDetectFragment.percent = percent
            }
            1 -> {
                CameraActivity.cameraActivity.afterDetectFragment.presenter.setTakePicture(CameraActivity.cameraActivity.afterDetectFragment, convBitmap, detectBitmap)
                Model.CLEAN_AFTER_EXIST = true
                if (percent>0.2){
                    CameraActivity.cameraActivity.afterDetectFragment.imageLight.visibility = View.VISIBLE
                    CameraActivity.cameraActivity.afterDetectFragment.imageLight.setImageDrawable(
                        ContextCompat.getDrawable(
                            CameraActivity.cameraActivity, R.drawable.red_light))
                }else{
                    CameraActivity.cameraActivity.afterDetectFragment.imageLight.visibility = View.VISIBLE
                    CameraActivity.cameraActivity.afterDetectFragment.imageLight.setImageDrawable(
                        ContextCompat.getDrawable(
                            CameraActivity.cameraActivity, R.drawable.green_light))
                }
                CameraActivity.cameraActivity.afterDetectFragment.percent = percent
            }
        }
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

    fun getDetectPicture(bitmap: Bitmap,mask2: Bitmap): Pair<Bitmap,Float> {
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