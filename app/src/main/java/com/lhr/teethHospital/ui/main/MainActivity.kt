package com.lhr.teethHospital.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
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
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.databinding.ActivityMainBinding
import com.lhr.teethHospital.file.CsvToSql
import com.lhr.teethHospital.model.Model.Companion.IMPORT_CSV
import com.lhr.teethHospital.ui.base.APP
import com.lhr.teethHospital.ui.base.BaseActivity
import com.lhr.teethHospital.ui.base.BaseViewModel
import com.lhr.teethHospital.ui.camera.CameraViewModel
import com.lhr.teethHospital.ui.login.LoginViewModel
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar

class MainActivity : BaseActivity(), View.OnClickListener {

    override val viewModel: MainViewModel by viewModels { (applicationContext as APP).appContainer.viewModelFactory }
    lateinit var binding: ActivityMainBinding
    lateinit var pageAdapter: ViewPageAdapter
    lateinit var personalManagerFragment: PersonalManagerFragment
    lateinit var repository: PersonalManagerRepository
    lateinit var settingFragment: SettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = PersonalManagerRepository.getInstance(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
                R.drawable.person_manager_icon_selector,
                R.drawable.setting_icon_selector
            )
            personalManagerFragment = PersonalManagerFragment()
            settingFragment = SettingFragment()
            var fragments = arrayListOf(
                personalManagerFragment,
                settingFragment,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMPORT_CSV) { // 使用之前定義的請求代碼
            if (resultCode == Activity.RESULT_OK) { // 檢查結果碼是否正常
                if (data != null) {

//                    val myData: Intent? = result.data
                    if (data != null) {
                        CsvToSql().csvToHospitalSql(this, data.data!!)
                        repository.getAllInfo()
//                        personalManagerFragment.viewModel.updateRecyclerInfo(personalManagerFragment.binding, personalManagerFragment)
                    }
                }
            }
        }
    }
}