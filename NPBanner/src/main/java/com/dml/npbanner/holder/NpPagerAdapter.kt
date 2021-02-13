package com.dml.npbanner.holder

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class NpPagerAdapter<T>(
    val mDatas: ArrayList<T>,
    private val holder: NPViewHolder<T>,
    private val canLoop: Boolean
) : PagerAdapter() {
    private var mViewPager: ViewPager? = null
    var mPageClickListener: BannerPageClickListener? = null
    private val mLooperCountFactor = 500

    /**
     * 初始化Adapter和设置当前选中的Item
     *
     * @param viewPager
     */
    fun setUpViewViewPager(viewPager: ViewPager?) {
        mViewPager = viewPager
        mViewPager?.adapter = this
        mViewPager?.adapter?.notifyDataSetChanged()
        val currentItem = if (canLoop) startSelectItem else 0
        //设置当前选中的Item
        mViewPager?.currentItem = currentItem
    }

    // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
    // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
    // 直到找到从0开始的位置
    private val startSelectItem: Int
        get() {
            if (realCount == 0) {
                return 0
            }
            // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
            // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
            var currentItem = realCount * mLooperCountFactor / 2
            if (currentItem % realCount == 0) {
                return currentItem
            }
            // 直到找到从0开始的位置
            while (currentItem % realCount != 0) {
                currentItem++
            }
            return currentItem
        }

    override fun getCount(): Int {
        // 2017.6.10 bug fix
        // 如果getCount 的返回值为Integer.MAX_VALUE 的话，那么在setCurrentItem的时候会ANR(除了在onCreate 调用之外)
        return if (canLoop) realCount * mLooperCountFactor else realCount //ViewPager返回int 最大值
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getView(position, container)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun finishUpdate(container: ViewGroup) {
        // 轮播模式才执行
        if (canLoop) {
            var position = mViewPager!!.currentItem
            if (position == count - 1) {
                position = 0
                setCurrentItem(position)
            }
        }
    }

    private fun setCurrentItem(position: Int) {
        try {
            mViewPager?.setCurrentItem(position, false)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * 获取真实的Count
     * @return
     */
    private val realCount: Int
        get() = mDatas.size

    private fun getView(position: Int, container: ViewGroup): View {
        val realPosition = position % realCount
        // create View
        val view = holder.createView(container.context)
        if (mDatas.size > 0) {
            holder.onBind(container.context, realPosition, mDatas[realPosition])
        }

        // 添加点击事件
        view.setOnClickListener { v ->
            mPageClickListener?.onPageClick(v, realPosition)
        }
        return view
    }

    init {
        //mDatas.add(datas.get(datas.size()-1));// 加入最后一个
        // mDatas.add(datas.get(0));//在最后加入最前面一个
    }
}