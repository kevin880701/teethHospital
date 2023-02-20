/**
 * @author Hao-Ran,Liu
 * @date 2021/06/27
 */
package com.example.teethHospital.util.DetectResult.DetectHistory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import com.example.teethHospital.Model.Model.Companion.mainActivity
import kotlinx.coroutines.*

class DetectHistoryPresenter(detectHistoryFragment: DetectHistoryFragment) {

    fun setDetectImage(detectFragment: DetectHistoryFragment, imageUri: Uri) {
        val originalParcelFileDescriptor = mainActivity.contentResolver.openFileDescriptor(imageUri, "r")
        val originalImage = BitmapFactory.decodeFileDescriptor(originalParcelFileDescriptor!!.fileDescriptor)
        detectFragment.imageOriginal.setImageBitmap(originalImage)
        val afterImage = detectFragment.presenter.getDetectPicture(originalImage)
        detectFragment.imageAfter.setImageBitmap(afterImage)
    }

    fun setTakePicture(detectFragment: DetectHistoryFragment, originalBitmap: Bitmap, afterBitmap: Bitmap) {
//        runBlocking {     // 阻塞主執行緒
//            launch(Dispatchers.IO) {
//                beforeDetectFragment.imageOriginal.setImageBitmap(originalBitmap)
////                beforeDetectFragment.imageAfter.setImageBitmap(afterBitmap)
//            }
//        }
//        takePicture2Activity.lifecycleScope.launch(Dispatchers.IO) {
//            beforeDetectFragment.imageOriginal.setImageBitmap(originalBitmap)
////                beforeDetectFragment.imageAfter.setImageBitmap(afterBitmap)
//        }
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                detectFragment.imageOriginal.setImageBitmap(originalBitmap)
                detectFragment.imageAfter.setImageBitmap(afterBitmap)
            }

        }
    }


    fun getDetectPicture(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 保存所有的像素的数组，图片宽×高
        val pixels = IntArray(width * height)
        val convPixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val clr = pixels[i]
            val R: Int = clr and 0xff0000 shr 16
            val G: Int = clr and 0x00ff00 shr 8
            val B: Int = clr and 0x0000ff shr 0
            val color: Int = Color.rgb(R, G, B)

            if (clr == Color.TRANSPARENT) {
                convPixels[i] = Color.TRANSPARENT
            } else if ((R in 249..253 && G in 71..164 && B in 143..199)) {
                convPixels[i] = Color.rgb(239, 88, 156)
            } else {
                convPixels[i] = Color.BLACK
            }
        }

        val convBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        convBitmap.setPixels(convPixels, 0, width, 0, 0, width, height)

        return convBitmap
    }
}