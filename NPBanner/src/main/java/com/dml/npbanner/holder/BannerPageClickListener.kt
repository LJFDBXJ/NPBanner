package com.dml.npbanner.holder

import android.view.View

/**
 * Banner page 点击回调
 */
interface BannerPageClickListener {
    fun onPageClick(view: View?, position: Int)
}