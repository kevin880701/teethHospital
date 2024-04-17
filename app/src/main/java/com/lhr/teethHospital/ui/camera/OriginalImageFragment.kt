package com.lhr.teethHospital.ui.camera

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentOriginalImageBinding
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseFragment
import com.lhr.teethHospital.ui.base.LoadState
import com.lhr.teethHospital.util.popupWindow.ChooseImagePopupWindow
import java.text.SimpleDateFormat
import java.util.Calendar

class OriginalImageFragment(private val originalBitmap: Bitmap? = null) : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentOriginalImageBinding? = null
    private val binding get() = _binding!!
    val viewModel: CameraViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(CameraViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOriginalImageBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        initView()
    }


    private fun initView() {
        if(originalBitmap == null){
            binding.buttonChoosePicture.visibility = View.VISIBLE
        }

        binding.imageOriginal.setImageBitmap(originalBitmap)
        binding.buttonChoosePicture.setOnClickListener(this)
    }

    private fun bindViewModel() {
        viewModel.originalImage.observe(viewLifecycleOwner) { bitmap ->
            if(bitmap == null){
                binding.buttonChoosePicture.visibility = View.VISIBLE
            }else{
                binding.buttonChoosePicture.visibility = View.GONE
            }
            binding.imageOriginal.setImageBitmap(bitmap)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonChoosePicture -> {
                val choose = ChooseImagePopupWindow(this.requireActivity())
                val view: View = LayoutInflater.from(this.requireContext()).inflate(
                    R.layout.popup_window_choose_image,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
            }
        }
    }


    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val myData: Intent? = result.data
            if (myData != null) {
                // 選擇圖片後變更圖片
//                viewModel.choosePhotoAlbum(this, myData.data!!)
//                isSetPicture = true
            }
        }
    }
}