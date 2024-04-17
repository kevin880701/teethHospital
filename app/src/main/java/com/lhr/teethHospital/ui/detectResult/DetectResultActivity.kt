package com.lhr.teethHospital.ui.detectResult

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lhr.teethHospital.model.Model.Companion.detectPictureFileName
import com.lhr.teethHospital.model.Model.Companion.originalPictureFileName
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.entity.RecordEntity
import com.lhr.teethHospital.model.Model.Companion.UPDATE_PATIENT_RECORD
import com.lhr.teethHospital.databinding.ActivityDetectResultBinding
import com.lhr.teethHospital.databinding.ActivityMemberInformationBinding
import com.lhr.teethHospital.model.Model.Companion.rangePictureFileName
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.ui.camera.DetectFragment
import com.lhr.teethHospital.ui.camera.OriginalImageFragment
import com.lhr.teethHospital.ui.memberInformation.MemberInformationViewModel
import com.lhr.teethHospital.util.viewPager.CameraViewPageAdapter
import java.io.File
import java.text.DecimalFormat

class DetectResultActivity : BaseActivity(), View.OnClickListener {

    override val viewModel: DetectResultViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }
    private var _binding: ActivityDetectResultBinding? = null
    private val binding get() = _binding!!
    lateinit var recordEntity: RecordEntity
    private lateinit var pageAdapter: CameraViewPageAdapter
    var originalBitmap: Bitmap? = null
    var rangeBitmap: Bitmap? = null
    var detectBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetectResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordEntity = intent.getSerializableExtra("recordEntity") as RecordEntity

        initView()

        // 使用正則表達式匹配同時包含 hospitalName 和 number 的字串
//        val pattern = Regex("(${Regex.escape(recordEntity.hospitalName)}.*?${Regex.escape(recordEntity.number)})")
        // 使用 replace 將匹配的字串替換為空字串
//        binding.textTitle.text = pattern.replace(recordEntity.fileName, "")
//        println(recordEntity.detectPercent.toFloat())


        // 如果小於等於0代表沒有拍攝照片(percent預設-1.0)
//        if(recordEntity.detectPercent.toFloat() >=0){
//            val originalBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + originalPictureFileName)
//            val originalBitmap: Bitmap =
//                BitmapFactory.decodeStream(contentResolver.openInputStream(originalBitmapFile.toUri()))
//            binding.imageOriginal.setImageBitmap(originalBitmap)
//            val afterBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + detectPictureFileName)
//            val afterBitmap: Bitmap =
//                BitmapFactory.decodeStream(contentResolver.openInputStream(afterBitmapFile.toUri()))
//            binding.imageDetect.setImageBitmap(afterBitmap)
//
//            if (recordEntity.detectPercent.toFloat() > 0.2) {
////                imageLight.visibility = View.VISIBLE
////                binding.imageLight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.red_light))
//            } else {
////                imageLight.visibility = View.VISIBLE
////                binding.imageLight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.green_light))
//            }
//
//            val df = DecimalFormat("00%")
//
//            binding.textPercent.text = "殘留量：" + df.format(recordEntity.detectPercent.toFloat())
//        }
    }

    private fun initView() {
        val originalBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + originalPictureFileName)
        if (originalBitmapFile.exists()) {
            originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(originalBitmapFile.toUri()))
        }
        val rangeBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + rangePictureFileName)
        if (rangeBitmapFile.exists()) {
            rangeBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(rangeBitmapFile.toUri()))
        }
        val detectBitmapFile = File(TEETH_DIR + recordEntity.fileName + "/" + detectPictureFileName)
        if (detectBitmapFile.exists()) {
            detectBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(detectBitmapFile.toUri()))
        }

        initTabLayout(binding.tabLayout)
        binding.titleBar.binding.imageBack.setOnClickListener(this)
//        binding.imageDelete.setOnClickListener(this)
    }

    private fun initTabLayout(tabLayoutMain: TabLayout) {
        tabLayoutMain.apply {
            var fragments = arrayListOf(
                OriginalImageFragment(originalBitmap),
                DetectFragment(rangeBitmap, detectBitmap),
            ) as ArrayList<Fragment>

            var tabText = arrayListOf(
                "原始圖片",
                "偵測圖片",
            )
            pageAdapter = CameraViewPageAdapter(supportFragmentManager, lifecycle, fragments)
            binding.viewPager.adapter = pageAdapter
            TabLayoutMediator(this, binding.viewPager) { tab, position ->
                tab.text = tabText[position]
            }.attach()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                finish()
            }

            R.id.imageDelete -> {
                viewModel.deleteDirectory(TEETH_DIR + recordEntity.fileName + "/", recordEntity, this)

                val intent = Intent(UPDATE_PATIENT_RECORD)
                sendBroadcast(intent)
                //更新歷史資訊資料
//                val dcimDir = File(PICTURE_DIR)
//                allFileList = dcimDir.listFiles()
//                historyActivity.historyAdapter.notifyDataSetChanged()

                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}