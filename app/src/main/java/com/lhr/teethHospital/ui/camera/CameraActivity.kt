package com.lhr.teethHospital.ui.camera

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.ActivityCameraBinding
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.model.Model.Companion.UPDATE_PATIENT_RECORD
import com.lhr.teethHospital.room.entity.HospitalEntity
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.util.createImageFile
import com.lhr.teethHospital.util.dialog.SaveRecordDialog
import com.lhr.teethHospital.util.loadImageAsBitmap
import com.lhr.teethHospital.util.popupWindow.ChooseImagePopupWindow
import com.lhr.teethHospital.util.toRequestBody
import com.lhr.teethHospital.util.viewPager.CameraViewPageAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar

class CameraActivity : BaseActivity(), View.OnClickListener {
    companion object {
        lateinit var takePicture: ActivityResultLauncher<Void>
    }

    public override val viewModel: CameraViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }

    private var _binding: ActivityCameraBinding? = null
    val binding get() = _binding!!
    private lateinit var pageAdapter: CameraViewPageAdapter
    lateinit var hospitalEntity: HospitalEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hospitalEntity = intent.getSerializableExtra(ROOT) as HospitalEntity

        takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            if (result != null) {
                val imageFile = createImageFile(this) // 创建一个临时文件用于保存图片
                val outputStream = FileOutputStream(imageFile)
                result.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // 将 Bitmap 保存到文件
                outputStream.close()

                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val params = HashMap<String, RequestBody>()
                params["file"] = requestBody

                viewModel.originalImage.postValue(result)
            }
        }


        initView()
        bindViewModel()
    }

    private fun initView() {
        initTabLayout(binding.tabLayout)
        binding.buttonImageDetect.setOnClickListener(this)
        binding.buttonCleanImage.setOnClickListener(this)
        binding.titleBar.binding.imageBack.setOnClickListener(this)
        binding.titleBar.binding.imageSave.setOnClickListener(this)
    }

    private fun bindViewModel() {
        viewModel.detectImage.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.tabLayout.getTabAt(1)?.select()
            }
        }
    }

    private fun initTabLayout(tabLayoutMain: TabLayout) {
        tabLayoutMain.apply {
            var fragments = arrayListOf(
                OriginalImageFragment(),
                DetectFragment(),
            ) as ArrayList<Fragment>

            var tabText = arrayListOf(
                "原始圖片",
                "偵測圖片",
            )
            pageAdapter = CameraViewPageAdapter(supportFragmentManager, lifecycle, fragments)
            binding.viewPager.adapter = pageAdapter
            TabLayoutMediator(this, binding.viewPager) { tab, position ->
//                tab.icon = ContextCompat.getDrawable(this.context, tabIconList[position])
                tab.text = tabText[position]
            }.attach()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                val intent = Intent(UPDATE_PATIENT_RECORD)
                sendBroadcast(intent)
                finish()
            }

            R.id.buttonImageDetect -> {
                if(viewModel.originalImage.value != null){
                    viewModel.showLoading()
                    viewModel.originalImage.value?.toRequestBody()?.let { viewModel.uploadImage(it) }
                }else{
                    Toast.makeText(this, "圖片未選擇", Toast.LENGTH_SHORT).show()
                }
            }
            // 圖片儲存
            R.id.imageSave -> {
//                if (viewModel.originalImage.value == null) {
//                    Toast.makeText(this, "圖片未選擇", Toast.LENGTH_SHORT).show()
//                } else {
//                    val customDialog = SaveRecordDialog(this)
//                    customDialog.show()
//                }
                if (viewModel.originalImage.value == null) {
                    Toast.makeText(this, "圖片未選擇", Toast.LENGTH_SHORT).show()
                } else {
                    // 獲取當前日期和時間
                    val calendar = Calendar.getInstance()
                    // 創建日期格式化對象，指定格式為 "yyyy-MM-dd HH:mm:ss"
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    viewModel.saveImage(hospitalEntity, dateFormat.format(calendar.time))
                    finish()
                }
            }

            R.id.buttonCleanImage -> {
                viewModel.cleanImage()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean { //捕捉返回鍵
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val myData: Intent? = result.data
            if (myData != null) {
                // 選擇圖片後變更圖片
//                viewModel.choosePhotoAlbum(this, myData.data!!)
//                isSetPicture = true
            }
        }
    }
}