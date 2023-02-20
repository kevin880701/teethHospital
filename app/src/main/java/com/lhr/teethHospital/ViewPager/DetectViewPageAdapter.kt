package com.lhr.teethHospital.ViewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lhr.teethHospital.util.Camera.Detect.DetectFragment

class DetectViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, beforeDetectFragment: DetectFragment, afterDetectFragment: DetectFragment) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    var fragments: ArrayList<Fragment> = arrayListOf(
        beforeDetectFragment,
        afterDetectFragment
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}