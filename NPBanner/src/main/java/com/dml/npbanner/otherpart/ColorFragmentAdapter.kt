package com.dml.npbanner.otherpart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

 class ColorFragmentAdapter(fm: FragmentManager, private val fragments: List<Fragment>) :
    FragmentPagerAdapter( fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}