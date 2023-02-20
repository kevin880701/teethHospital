package com.example.teethHospital.util.Camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.teethHospital.Model.Model
import com.example.teethHospital.Model.Model.Companion.CLEAN_AFTER_EXIST
import com.example.teethHospital.Model.Model.Companion.CLEAN_BEFORE_EXIST
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.R
import com.example.teethHospital.Room.HospitalEntity
import com.example.teethHospital.ViewPager.DetectViewPageAdapter
import com.example.teethHospital.util.Camera.Detect.DetectFragment
import com.example.teethHospital.util.CameraPresenter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class CameraActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        lateinit var cameraActivity: CameraActivity
    }

    lateinit var imageBack: ImageView
    lateinit var viewPager: ViewPager2
    lateinit var tabLayoutPicture: TabLayout
    lateinit var tabTitleList: ArrayList<String>
    lateinit var buttonChooseImage: Button
    lateinit var buttonSaveRecord: Button
    lateinit var buttonCleanImage: Button

    lateinit var presnter: CameraPresenter
    lateinit var beforeDetectFragment: DetectFragment
    lateinit var afterDetectFragment: DetectFragment
    lateinit var pageAdapter: DetectViewPageAdapter
    lateinit var hospitalEntity: HospitalEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar!!.hide()

        imageBack = findViewById(R.id.imageBack)
        viewPager = findViewById(R.id.viewPager)
        tabLayoutPicture = findViewById(R.id.tabLayoutPicture)
        buttonChooseImage = findViewById(R.id.buttonImportSingle)
        buttonSaveRecord = findViewById(R.id.buttonSaveRecord)
        buttonCleanImage = findViewById(R.id.buttonCleanImage)
        presnter = CameraPresenter(this)

        hospitalEntity = intent.getSerializableExtra("hospitalEntity") as HospitalEntity

        cameraActivity = this
        tabTitleList =
            resources.getStringArray(R.array.clean).toCollection(ArrayList())

        beforeDetectFragment = DetectFragment()
        afterDetectFragment = DetectFragment()
        pageAdapter = DetectViewPageAdapter(supportFragmentManager, lifecycle, beforeDetectFragment, afterDetectFragment)
        viewPager.adapter = pageAdapter
        TabLayoutMediator(tabLayoutPicture, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

        buttonChooseImage.setOnClickListener(this)
        buttonSaveRecord.setOnClickListener(this)
        buttonCleanImage.setOnClickListener(this)
        imageBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
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
            R.id.buttonSaveRecord -> {
                if(!CLEAN_BEFORE_EXIST || !CLEAN_AFTER_EXIST){
                    Toast.makeText(mainActivity, "圖片未選擇", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(mainActivity, "儲存紀錄", Toast.LENGTH_SHORT).show()
                    presnter.saveRecord(hospitalEntity)
                    finish()
                }
            }
            R.id.buttonCleanImage -> {
                presnter.cleanImage()
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
            Log.v("PPPPPPPPPPPP","：" + result.resultCode)
            val myData: Intent? = result.data
            if (myData != null) {
                // 選擇圖片後變更圖片
                when (viewPager.currentItem) {
                    0 -> {
                        beforeDetectFragment.presenter.setDetectImage(beforeDetectFragment,myData.data!!)
                        CLEAN_BEFORE_EXIST = true
                    }
                    1-> {
                        afterDetectFragment.presenter.setDetectImage(afterDetectFragment,myData.data!!)
                        CLEAN_AFTER_EXIST = true
                    }
                }
            }else{
                Log.v("PPPPPPPPPPPP","LLLLLLLLLLLLLLLLLLL")
            }
        }
    }
}