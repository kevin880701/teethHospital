package com.lhr.teethHospital.ui.detectResult.detectHistory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentDetectHistoryBinding
import java.io.File
import java.text.DecimalFormat

class DetectHistoryFragment(originalUri: String, afterUri: String, percent: Float): Fragment() {
    lateinit var viewModel: DetectHistoryViewModel
    lateinit var binding: FragmentDetectHistoryBinding
    val originalUri = originalUri
    val afterUri = afterUri
    var percent = percent
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detect_history, container, false
        )
        val view: View = binding!!.root

        viewModel = ViewModelProvider(
            this,
            DetectHistoryViewModelFactory(this.requireActivity().application)
        )[DetectHistoryViewModel::class.java]
        binding.viewModel = viewModel

        // 如果小於等於0代表沒有拍攝照片(percent預設-1.0)
        if(percent.toFloat() >=0){
            val originalBitmapFile = File(originalUri)
            val originalBitmap: Bitmap =
                BitmapFactory.decodeStream(this.requireActivity().contentResolver.openInputStream(originalBitmapFile.toUri()))
            binding.imageOriginal.setImageBitmap(originalBitmap)

            val afterBitmapFile = File(afterUri)
            val afterBitmap: Bitmap =
                BitmapFactory.decodeStream(this.requireActivity().contentResolver.openInputStream(afterBitmapFile.toUri()))
            binding.imageAfter.setImageBitmap(afterBitmap)

            if (percent > 0.2) {
//                imageLight.visibility = View.VISIBLE
                binding.imageLight.setImageDrawable(ContextCompat.getDrawable(this.requireActivity(), R.drawable.red_light))
            } else {
//                imageLight.visibility = View.VISIBLE
                binding.imageLight.setImageDrawable(ContextCompat.getDrawable(this.requireActivity(), R.drawable.green_light))
            }

            val df = DecimalFormat("00%")
            binding.textPercent.text = "殘留量：" + df.format(percent)
        }
        return view
    }
}