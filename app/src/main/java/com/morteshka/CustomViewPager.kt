package com.morteshka

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.viewpager.widget.ViewPager
import java.util.*

/**
 * Created by zhouwei on 17/8/16.
 */
class CustomViewPager : ViewPager {
    private val childCenterXAbs = ArrayList<Int>()
    private val childIndex = SparseArray<Int>()

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    private fun init() {
        clipToPadding = false
        overScrollMode = OVER_SCROLL_NEVER
    }

    /**
     * @param childCount
     * @param n
     * @return 第n个位置的child 的绘制索引
     */
    override fun getChildDrawingOrder(childCount: Int, n: Int): Int {
        if (n == 0 || childIndex.size() != childCount) {
            childCenterXAbs.clear()
            childIndex.clear()
            val viewCenterX = getViewCenterX(this)
            for (i in 0 until childCount) {
                var indexAbs = Math.abs(viewCenterX - getViewCenterX(getChildAt(i)))
                //两个距离相同，后来的那个做自增，从而保持abs不同
                if (childIndex[indexAbs] != null) {
                    ++indexAbs
                }
                childCenterXAbs.add(indexAbs)
                childIndex.append(indexAbs, i)
            }
            childCenterXAbs.sort() //1,0,2  0,1,2
        }
        //那个item距离中心点远一些，就先draw它。（最近的就是中间放大的item,最后draw）
        return childIndex[childCenterXAbs[childCount - 1 - n]]
    }

    private fun getViewCenterX(view: View): Int {
        val array = IntArray(2)
        view.getLocationOnScreen(array)
        return array[0] + view.width / 2
    }
}