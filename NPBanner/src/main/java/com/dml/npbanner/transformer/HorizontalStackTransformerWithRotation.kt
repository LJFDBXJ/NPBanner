package com.dml.npbanner.transformer

import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.dml.npbanner.util.DensityUtil.dp2px
import kotlin.math.floor

class HorizontalStackTransformerWithRotation(private val boundViewPager: ViewPager) :
    ViewPager.PageTransformer {
    private val offscreenPageLimit: Int = boundViewPager.offscreenPageLimit
    override fun transformPage(view: View, position: Float) {
        val pagerWidth = boundViewPager.width
        val horizontalOffsetBase =
            (pagerWidth - pagerWidth * CENTER_PAGE_SCALE) / 2 / offscreenPageLimit + dp2px(
                view.context,
                15f
            )
        if (position >= offscreenPageLimit || position <= -1) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
        if (position >= 0) {
            val translationX = (horizontalOffsetBase - view.width) * position
            view.translationX = translationX
        }
        if (position > -1 && position < 0) {
            val rotation = position * 30
            view.rotation = rotation
            view.alpha = position * position * position + 1
        } else if (position > offscreenPageLimit - 1) {
            view.alpha = (1 - position + floor(position.toDouble())).toFloat()
        } else {
            view.rotation = 0f
            view.alpha = 1f
        }
        if (position == 0f) {
            view.scaleX = CENTER_PAGE_SCALE
            view.scaleY = CENTER_PAGE_SCALE
        } else {
            val scaleFactor = (CENTER_PAGE_SCALE - position * 0.1f).coerceAtMost(CENTER_PAGE_SCALE)
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
        }

        // test code: view初始化时，设置了tag
        val tag = view.tag as String
        //        LogUtil.e("viewTag" + tag, "viewTag: " + (String) view.getTag() + " --- transformerPosition: " + position + " --- floor: " + Math.floor(position) + " --- childCount: "+ boundViewPager.getChildCount());
        ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5)
    }

    companion object {
        private const val CENTER_PAGE_SCALE = 0.8f
    }

}