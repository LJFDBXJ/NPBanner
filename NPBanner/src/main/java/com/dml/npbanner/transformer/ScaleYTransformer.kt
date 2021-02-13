package com.dml.npbanner.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * Created by zhouwei on 17/5/26.
 */
class ScaleYTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1.0f -> {
                page.scaleY = MIN_SCALE
            }
            position <= 1f -> {
                val scale = MIN_SCALE.coerceAtLeast(1 - abs(position))
                page.scaleY = scale
                page.scaleX = scale

                if(position<0){
                    page.translationX = page.width * (1 - scale) /2;
                }else{
                    page.translationX = -page.width * (1 - scale) /2;
                }
            }
            else -> {
                page.scaleY = MIN_SCALE
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.9f
    }
}