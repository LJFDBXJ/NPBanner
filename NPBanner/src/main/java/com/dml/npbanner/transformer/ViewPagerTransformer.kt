package com.dml.npbanner.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager

class ViewPagerTransformer : ViewPager.PageTransformer  {
    override fun transformPage(page: View, position: Float) {
          page.rotationY = position * -30f
    }
}