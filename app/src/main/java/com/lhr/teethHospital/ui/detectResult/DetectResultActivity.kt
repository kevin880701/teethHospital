package com.lhr.teethHospital.ui.detectResult

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.lhr.teethHospital.model.Model.Companion.CLEAN_AFTER_DETECT
import com.lhr.teethHospital.model.Model.Companion.CLEAN_AFTER_ORIGINAL
import com.lhr.teethHospital.model.Model.Companion.CLEAN_BEFORE_DETECT
import com.lhr.teethHospital.model.Model.Companion.CLEAN_BEFORE_ORIGINAL
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.R
import com.lhr.teethHospital.room.RecordEntity
import com.lhr.teethHospital.ui.detectResult.detectHistory.DetectHistoryFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lhr.teethHospital.model.Model.Companion.UPDATE_PATIENT_RECORD
import com.lhr.teethHospital.databinding.ActivityDetectResultBinding
import com.lhr.teethHospital.viewPager.ViewPageAdapter


class DetectResultActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var viewModel: DetectResultViewModel
    lateinit var binding: ActivityDetectResultBinding
    lateinit var tabTitleList: ArrayList<String>
    lateinit var pageAdapter: ViewPageAdapter
    lateinit var recordEntity: RecordEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detect_result)
        viewModel = ViewModelProvider(
            this,
            DetectResultViewModelFactory(this.application)
        )[DetectResultViewModel::class.java]
        binding.viewModel = viewModel
        
        recordEntity = intent.getSerializableExtra("recordEntity") as RecordEntity
        var dateArray = recordEntity.recordDate.split("-")
        binding.textFileName.text = "${dateArray[1]}年${dateArray[2]}月${dateArray[3]}日 ${dateArray[4]}點${dateArray[5]}分${dateArray[6]}秒"
//        textFileName.text = recordEntity.recordDate

        initTabLayout(binding.tabLayoutPicture)
        binding.imageBack.setOnClickListener(this)
        binding.imageDelete.setOnClickListener(this)
    }

    fun initTabLayout(tabLayout: TabLayout) {
        tabLayout.apply {
            tabTitleList = resources.getStringArray(R.array.clean).toCollection(ArrayList())
            var fragments = arrayListOf(
                DetectHistoryFragment(TEETH_DIR + recordEntity.fileName + "/" + CLEAN_BEFORE_ORIGINAL, TEETH_DIR + recordEntity.fileName + "/" + CLEAN_BEFORE_DETECT, recordEntity.beforePercent.toFloat()),
                DetectHistoryFragment(TEETH_DIR + recordEntity.fileName + "/" + CLEAN_AFTER_ORIGINAL, TEETH_DIR + recordEntity.fileName + "/" + CLEAN_AFTER_DETECT, recordEntity.afterPercent.toFloat()),
            ) as ArrayList<Fragment>
            pageAdapter = ViewPageAdapter(this@DetectResultActivity.supportFragmentManager, lifecycle, fragments)
            binding.viewPager.adapter = pageAdapter
            TabLayoutMediator(this, binding.viewPager) { tab, position ->
                tab.text = tabTitleList[position]
            }.attach()
            binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
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