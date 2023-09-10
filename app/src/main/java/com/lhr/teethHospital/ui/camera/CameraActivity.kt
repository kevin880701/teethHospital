package com.lhr.teethHospital.ui.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lhr.teethHospital.model.Model.Companion.CLEAN_AFTER_EXIST
import com.lhr.teethHospital.model.Model.Companion.CLEAN_BEFORE_EXIST
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.ui.camera.detect.DetectFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lhr.teethHospital.model.Model.Companion.UPDATE_PATIENT_RECORD
import com.lhr.teethHospital.databinding.ActivityCameraBinding
import com.lhr.teethHospital.model.Model.Companion.ROOT
import com.lhr.teethHospital.popupWindow.ChooseImagePopupWindow
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.viewPager.ViewPageAdapter


class CameraActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        lateinit var cameraActivity: CameraActivity
    }

    lateinit var viewModel: CameraViewModel
    lateinit var binding: ActivityCameraBinding
    lateinit var tabTitleList: ArrayList<String>
    lateinit var fragments: ArrayList<Fragment>
    lateinit var dataBase: SqlDatabase
//    lateinit var beforeDetectFragment: DetectFragment
//    lateinit var afterDetectFragment: DetectFragment
    lateinit var pageAdapter: ViewPageAdapter
    lateinit var hospitalEntity: HospitalEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        viewModel = ViewModelProvider(
            this,
            CameraViewModelFactory(this.application)
        )[CameraViewModel::class.java]
        binding.viewModel = viewModel
        dataBase = SqlDatabase(this)

        hospitalEntity = intent.getSerializableExtra(ROOT) as HospitalEntity

        cameraActivity = this

        initTabLayout(binding.tabLayoutPicture)

        binding.buttonImportSingle.setOnClickListener(this)
        binding.buttonSaveRecord.setOnClickListener(this)
        binding.buttonCleanImage.setOnClickListener(this)
        binding.imageBack.setOnClickListener(this)
    }

    private fun initTabLayout(tabLayout: TabLayout) {
        tabLayout.apply {
            tabTitleList =
                resources.getStringArray(R.array.clean).toCollection(ArrayList())

            fragments = arrayListOf(
                DetectFragment(),
                DetectFragment()
            ) as ArrayList<Fragment>
//            beforeDetectFragment = DetectFragment()
//            afterDetectFragment = DetectFragment()
            pageAdapter = ViewPageAdapter(supportFragmentManager, lifecycle, fragments)
            binding.viewPager.adapter = pageAdapter
            TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
                tab.text = tabTitleList[position]
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
            R.id.buttonImportSingle -> {
                val choose = ChooseImagePopupWindow(this)
                val view: View = LayoutInflater.from(this).inflate(
                    R.layout.popup_window_choose_image,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
                //強制隱藏鍵盤
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
            // 圖片儲存
            R.id.buttonSaveRecord -> {
                if(!(CLEAN_BEFORE_EXIST || CLEAN_AFTER_EXIST)){
                    Toast.makeText(this, "圖片未選擇", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "儲存紀錄", Toast.LENGTH_SHORT).show()
                    viewModel.saveRecord(hospitalEntity, fragments, dataBase)
                    sendBroadcast(
                        Intent(UPDATE_PATIENT_RECORD).putExtra("action", UPDATE_PATIENT_RECORD)
                    )
                    finish()
                }
            }
            R.id.buttonCleanImage -> {
                viewModel.cleanImage(pageAdapter, binding.viewPager)
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
                val currentFragment = pageAdapter.fragments[binding.viewPager.currentItem] as DetectFragment
                currentFragment.viewModel.setDetectImage(currentFragment,myData.data!!)
                when(binding.viewPager.currentItem){
                    0 -> {
                        CLEAN_BEFORE_EXIST = true
                    }
                    1-> {
                        CLEAN_AFTER_EXIST = true
                    }
                }
            }
        }
    }
}