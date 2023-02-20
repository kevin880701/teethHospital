package com.example.teethHospital.util.DetectResult

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.teethHospital.Model.Model.Companion.CLEAN_AFTER_DETECT
import com.example.teethHospital.Model.Model.Companion.CLEAN_AFTER_ORIGINAL
import com.example.teethHospital.Model.Model.Companion.CLEAN_BEFORE_DETECT
import com.example.teethHospital.Model.Model.Companion.CLEAN_BEFORE_ORIGINAL
import com.example.teethHospital.Model.Model.Companion.PICTURE_DIR
import com.example.teethHospital.R
import com.example.teethHospital.Room.RecordEntity
import com.example.teethHospital.ViewPager.HistoryPageAdapter
import com.example.teethHospital.util.DetectResult.DetectHistory.DetectHistoryFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DetectResultActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        lateinit var detectResultActivity: DetectResultActivity
    }

    lateinit var imageBack: ImageView
    lateinit var imageDelete: ImageView
    lateinit var textFileName: TextView
    lateinit var presenter: DetectResultPresenter
    lateinit var viewPager: ViewPager2
    lateinit var tabLayoutPicture: TabLayout
    lateinit var tabTitleList: ArrayList<String>
    lateinit var pageAdapter: HistoryPageAdapter
    lateinit var recordEntity: RecordEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_result)
        supportActionBar!!.hide()
        detectResultActivity = this
        presenter = DetectResultPresenter(this)
        imageBack = findViewById(R.id.imageBack)
        imageDelete = findViewById(R.id.imageDelete)
        textFileName = findViewById(R.id.textFileName)
        viewPager = findViewById(R.id.viewPager)
        tabLayoutPicture = findViewById(R.id.tabLayoutPicture)

        recordEntity = intent.getSerializableExtra("recordEntity") as RecordEntity
        var dateArray = recordEntity.recordDate.split("-")
        textFileName.text = "${dateArray[1]}年${dateArray[2]}月${dateArray[3]}日 ${dateArray[4]}點${dateArray[5]}分${dateArray[6]}秒"
//        textFileName.text = recordEntity.recordDate

        tabTitleList =
            resources.getStringArray(R.array.clean).toCollection(ArrayList())

        var beforeDetectHistoryFragment = DetectHistoryFragment(PICTURE_DIR + recordEntity.fileName + "/" + CLEAN_BEFORE_ORIGINAL, PICTURE_DIR + recordEntity.fileName + "/" + CLEAN_BEFORE_DETECT, recordEntity.beforePercent.toFloat())
        var afterDetectHistoryFragment = DetectHistoryFragment(PICTURE_DIR + recordEntity.fileName + "/" + CLEAN_AFTER_ORIGINAL, PICTURE_DIR + recordEntity.fileName + "/" + CLEAN_AFTER_DETECT, recordEntity.afterPercent.toFloat())
        pageAdapter = HistoryPageAdapter(this.supportFragmentManager, lifecycle, beforeDetectHistoryFragment, afterDetectHistoryFragment)
        viewPager.adapter = pageAdapter
        TabLayoutMediator(tabLayoutPicture, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

        imageBack.setOnClickListener(this)
        imageDelete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                finish()
            }
            R.id.imageDelete -> {
                presenter.deleteDirectory(PICTURE_DIR + recordEntity.fileName + "/", recordEntity)

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