package com.lhr.teethHospital.ui.main

import android.content.BroadcastReceiver
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.lhr.teethHospital.R
import com.lhr.teethHospital.viewPager.ViewPageAdapter
import com.lhr.teethHospital.ui.personalManager.PersonalManagerFragment
import com.lhr.teethHospital.ui.setting.SettingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lhr.teethHospital.databinding.ActivityMainBinding
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.isPersonalManagerBack

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var pageAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(this.application)
        )[MainViewModel::class.java]
        binding.viewModel = viewModel

        isProgressBar.observe(this) { isProgressBar ->
            if(isProgressBar){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.INVISIBLE
            }
        }


        initTabLayout(binding.tabLayout)
    }

    fun initTabLayout(tabLayoutMain: TabLayout) {
        tabLayoutMain.apply {
            var tabIconList = arrayListOf(
                R.drawable.person_manager,
                R.drawable.setting
            )
            var fragments = arrayListOf(
                PersonalManagerFragment(),
                SettingFragment(),
            ) as ArrayList<Fragment>
            pageAdapter = ViewPageAdapter(supportFragmentManager, lifecycle, fragments)
            binding.viewPager.adapter = pageAdapter
            TabLayoutMediator(this, binding.viewPager) { tab, position ->
                tab.icon = ContextCompat.getDrawable(this.context, tabIconList[position])
            }.attach()
            binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        }
    }

    override fun onClick(v: View?) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                when (pageAdapter.fragments[binding.viewPager.currentItem]) {
                    is PersonalManagerFragment -> {
                        isPersonalManagerBack.value = true
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