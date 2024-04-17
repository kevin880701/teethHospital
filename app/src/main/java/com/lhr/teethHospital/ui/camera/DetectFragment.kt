package com.lhr.teethHospital.ui.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentDetectBinding
import com.lhr.teethHospital.ui.base.BaseFragment

class DetectFragment(private val rangeBitmap: Bitmap? = null, private val detectBitmap: Bitmap? = null) : BaseFragment() {

    private var _binding: FragmentDetectBinding? = null
    private val binding get() = _binding!!
    val viewModel: CameraViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(CameraViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetectBinding.inflate(layoutInflater)
        val view: View = binding!!.root


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
    }

    private fun initView() {
        binding.imageRange.setImageBitmap(rangeBitmap)
        binding.imageDetect.setImageBitmap(detectBitmap)
    }

    private fun bindViewModel() {
        viewModel.rangeImage.observe(viewLifecycleOwner) { bitmap ->
            binding.imageRange.setImageBitmap(bitmap)
        }
        viewModel.detectImage.observe(viewLifecycleOwner) { bitmap ->
            binding.imageDetect.setImageBitmap(bitmap)
        }
        viewModel.detectPercent.observe(viewLifecycleOwner) { text ->
            binding.textPercent.text = text
        }
    }
}