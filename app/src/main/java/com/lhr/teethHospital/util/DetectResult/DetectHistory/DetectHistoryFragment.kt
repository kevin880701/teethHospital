package com.lhr.teethHospital.util.DetectResult.DetectHistory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.R
import com.lhr.teethHospital.util.DetectResult.DetectResultActivity.Companion.detectResultActivity
import java.io.File
import java.text.DecimalFormat


class DetectHistoryFragment(originalUri: String, afterUri: String, percent: Float): Fragment() {

    lateinit var imageOriginal: ImageView
    lateinit var imageAfter: ImageView
    lateinit var imageLight: ImageView
    lateinit var textPercent: TextView
    lateinit var presenter: DetectHistoryPresenter
    val originalUri = originalUri
    val afterUri = afterUri
    var percent = percent
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_detect_result, container, false)
        presenter = DetectHistoryPresenter(this)
        imageOriginal = view.findViewById(R.id.imageOriginal)
        imageAfter = view.findViewById(R.id.imageAfter)
        imageLight = view.findViewById(R.id.imageLight)
        textPercent = view.findViewById(R.id.textPercent)
        // 如果小於等於0代表沒有拍攝照片(percent預設-1.0)
        if(percent.toFloat() >=0){
            val originalBitmapFile = File(originalUri)
            val originalBitmap: Bitmap =
                BitmapFactory.decodeStream(detectResultActivity.contentResolver.openInputStream(originalBitmapFile.toUri()))
            imageOriginal.setImageBitmap(originalBitmap)

            val afterBitmapFile = File(afterUri)
            val afterBitmap: Bitmap =
                BitmapFactory.decodeStream(detectResultActivity.contentResolver.openInputStream(afterBitmapFile.toUri()))
            imageAfter.setImageBitmap(afterBitmap)

            if (percent > 0.2) {
                imageLight.visibility = View.VISIBLE
                imageLight.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.red_light))
            } else {
                imageLight.visibility = View.VISIBLE
                imageLight.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.green_light))
            }

            val df = DecimalFormat("00%")
            textPercent.text = "殘留量：" + df.format(percent)
        }
        return view
    }
}