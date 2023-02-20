package com.lhr.teethHospital.ViewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lhr.teethHospital.util.DetectResult.DetectHistory.DetectHistoryFragment

class HistoryPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, beforeDetectHistoryFragment: DetectHistoryFragment, afterDetectHistoryFragment: DetectHistoryFragment) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    var fragments: ArrayList<Fragment> = arrayListOf(
        beforeDetectHistoryFragment,
        afterDetectHistoryFragment
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}