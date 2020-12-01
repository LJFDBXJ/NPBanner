package com.morteshka.holder

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * ＊由于ViewPager 默认的切换速度有点快，因此用一个Scroller 来控制切换的速度
 * 而实际上ViewPager 切换本来就是用的Scroller来做的，因此我们可以通过反射来
 * 获取取到ViewPager 的 mScroller 属性，然后替换成我们自己的Scroller
 */
class ViewPagerScroller : Scroller {
    var scrollDuration = 800 // ViewPager默认的最大Duration 为600,我们默认稍微大一点。值越大越慢。
        private set
    var isUseDefaultDuration = false

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, interpolator: Interpolator?) : super( context, interpolator)
    constructor(context: Context?, interpolator: Interpolator?, flywheel: Boolean) : super(
        context,
        interpolator,
        flywheel
    )

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, scrollDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(
            startX,
            startY,
            dx,
            dy,
            if (isUseDefaultDuration) duration else scrollDuration
        )
    }

    fun setDuration(duration: Int) {
        scrollDuration = duration
    }
}