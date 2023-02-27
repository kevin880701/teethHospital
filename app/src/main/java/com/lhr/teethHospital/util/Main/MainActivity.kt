package com.lhr.teethHospital.util.Main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.lhr.teethHospital.Model.FileManager
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.Model.Model.Companion.personalManagerFragment
import com.lhr.teethHospital.Model.Model.Companion.settingFragment
import com.lhr.teethHospital.R
import com.lhr.teethHospital.ViewPager.MainViewPageAdapter
import com.lhr.teethHospital.util.MainPresenter
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment
import com.lhr.teethHospital.util.Setting.SettingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var tabIconList: ArrayList<Int>
    lateinit var presenter: MainPresenter
    lateinit var progressBar: ProgressBar
    lateinit var imageViewBlack: ImageView
    lateinit var viewPager: ViewPager2
    lateinit var tabLayoutCommodity: TabLayout
    lateinit var pageAdapter: MainViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        mainActivity = this
        presenter = MainPresenter(this)

        viewPager = findViewById(R.id.viewPager)
        tabLayoutCommodity = findViewById(R.id.tabLayoutCommodity)
        progressBar = findViewById(R.id.progressBar)
        imageViewBlack = findViewById(R.id.imageViewBlack)

        personalManagerFragment = PersonalManagerFragment()
        settingFragment = SettingFragment()

        tabIconList = arrayListOf(R.drawable.person_manager, R.drawable.setting)

        pageAdapter = MainViewPageAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = pageAdapter
        TabLayoutMediator(tabLayoutCommodity, viewPager) { tab, position ->
            tab.icon = ContextCompat.getDrawable(this, tabIconList[position])
        }.attach()
    }

    override fun onClick(v: View?) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                when (pageAdapter.fragments[viewPager.currentItem]) {
                    is PersonalManagerFragment -> {
                        var personalManagerFragment = pageAdapter.fragments[viewPager.currentItem] as PersonalManagerFragment
                        personalManagerFragment.presenter.back()
                    }
                    is SettingFragment -> {
                        finish()
                    }
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}