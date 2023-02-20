package com.example.teethHospital.util.Main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.teethHospital.Model.FileManager
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.Model.Model.Companion.personalManagerFragment
import com.example.teethHospital.Model.Model.Companion.settingFragment
import com.example.teethHospital.R
import com.example.teethHospital.ViewPager.MainViewPageAdapter
import com.example.teethHospital.util.MainPresenter
import com.example.teethHospital.util.PersonalManager.PersonalManagerFragment
import com.example.teethHospital.util.Setting.SettingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var tabIconList: ArrayList<Int>
    lateinit var presenter: MainPresenter
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