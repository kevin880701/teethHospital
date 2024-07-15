package com.lhr.teethHospital.ui.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentOriginalImageBinding
import com.lhr.teethHospital.ui.base.BaseFragment
import com.lhr.teethHospital.util.createImageFile
import com.lhr.teethHospital.util.popupWindow.ChooseImagePopupWindow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileOutputStream
import java.io.InputStream

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
                val choose = ChooseImagePopupWindow(this.requireActivity(), this)
                val view: View = LayoutInflater.from(this.requireContext()).inflate(
                    R.layout.popup_window_choose_image,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
            }
        }
    }


    val selectFromAlbumResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data

            if (selectedImageUri != null) {
                // 从相册选择的图片
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(selectedImageUri)
                viewModel.originalImage.postValue(BitmapFactory.decodeStream(inputStream))
//                b = BitmapFactory.decodeStream(inputStream)
            }
//            else if (photoURI != null) {
//                // 拍照得到的图片
//                b = BitmapFactory.decodeFile(photoURI?.path)
//            }

        }
    }

}