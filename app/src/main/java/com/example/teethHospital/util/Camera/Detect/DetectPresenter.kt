/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.example.teethHospital.util.Camera.Detect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.R
import com.example.teethHospital.util.Camera.CameraActivity.Companion.cameraActivity
import kotlinx.coroutines.*
import java.text.DecimalFormat

class DetectPresenter(detectFragment: DetectFragment) {
    fun setDetectImage(detectFragment: DetectFragment, imageUri: Uri) {
        val originalParcelFileDescriptor = mainActivity.contentResolver.openFileDescriptor(imageUri, "r")
        val originalImage = BitmapFactory.decodeFileDescriptor(originalParcelFileDescriptor!!.fileDescriptor)
        detectFragment.imageOriginal.setImageBitmap(originalImage)
        val (afterImage, percent) = detectFragment.presenter.getDetectPicture(originalImage)
        detectFragment.imageAfter.setImageBitmap(afterImage)
        if (percent>0.2){
            detectFragment.imageLight.visibility = View.VISIBLE
            detectFragment.imageLight.setImageDrawable(ContextCompat.getDrawable(cameraActivity, R.drawable.red_light))
        }else{
            detectFragment.imageLight.visibility = View.VISIBLE
            detectFragment.imageLight.setImageDrawable(ContextCompat.getDrawable(cameraActivity, R.drawable.green_light))
        }
        val df = DecimalFormat("00%")
        detectFragment.textPercent.text = "殘留量：" + df.format(percent)
        detectFragment.percent = percent
    }

    fun setTakePicture(detectFragment: DetectFragment, originalBitmap: Bitmap, afterBitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                detectFragment.imageOriginal.setImageBitmap(originalBitmap)
                detectFragment.imageAfter.setImageBitmap(afterBitmap)
            }

        }
    }

    fun getDetectPicture(bitmap: Bitmap):Pair<Bitmap,Float>  {
        val width = bitmap.width
        val height = bitmap.height
        var count = 0.0F

        // 保存所有的像素的数组，图片宽×高
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
                convPixels[i] = Color.rgb(247, 247, 247)
            }
            if ((R in 109..187 && G in 50..130 && B in 57..148)) {
                convPixels[i] = Color.rgb(247, 247, 247)
                count--
            }
        }

        val convBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        convBitmap.setPixels(convPixels, 0, width, 0, 0, width, height)
        var percent = (count)/(width * height)
        return Pair(convBitmap, percent)
    }
}