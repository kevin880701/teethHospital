package com.lhr.teethHospital.ViewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lhr.teethHospital.Model.Model
import com.lhr.teethHospital.Model.Model.Companion.personalManagerFragment
import com.lhr.teethHospital.Model.Model.Companion.settingFragment
import com.lhr.teethHospital.util.PersonalManager.PersonalManagerFragment
import com.lhr.teethHospital.util.Setting.SettingFragment

class MainViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    var fragments: ArrayList<Fragment> = arrayListOf(
        personalManagerFragment,
        settingFragment,
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}