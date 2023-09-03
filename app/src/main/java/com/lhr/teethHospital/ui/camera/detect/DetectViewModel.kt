package com.lhr.teethHospital.ui.camera.detect

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lhr.teethHospital.R
import com.lhr.teethHospital.ui.camera.CameraActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class DetectViewModel(application: Application) : AndroidViewModel(application) {

    fun setDetectImage(detectFragment: DetectFragment, imageUri: Uri) {
        val originalParcelFileDescriptor = detectFragment.requireActivity().contentResolver.openFileDescriptor(imageUri, "r")
        val originalImage = BitmapFactory.decodeFileDescriptor(originalParcelFileDescriptor!!.fileDescriptor)
        detectFragment.binding.imageOriginal.setImageBitmap(originalImage)
        val (afterImage, percent) = detectFragment.viewModel.getDetectPicture(originalImage)
        detectFragment.binding.imageAfter.setImageBitmap(afterImage)
        if (percent>0.2){
//            detectFragment.imageLight.visibility = View.VISIBLE
            detectFragment.binding.imageLight.setImageDrawable(ContextCompat.getDrawable(CameraActivity.cameraActivity, R.drawable.red_light))
        }else{
//            detectFragment.imageLight.visibility = View.VISIBLE
            detectFragment.binding.imageLight.setImageDrawable(ContextCompat.getDrawable(CameraActivity.cameraActivity, R.drawable.green_light))
        }
        val df = DecimalFormat("00%")
        detectFragment.binding.textPercent.text = "殘留量：" + df.format(percent)
        detectFragment.percent = percent
    }

    fun setTakePicture(detectFragment: DetectFragment, originalBitmap: Bitmap, afterBitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                detectFragment.binding.imageOriginal.setImageBitmap(originalBitmap)
                detectFragment.binding.imageAfter.setImageBitmap(afterBitmap)
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
}