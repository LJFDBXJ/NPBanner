package com.morteshka

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.morteshka.holder.*
import com.morteshka.mooseman.coustombanner.R
import com.morteshka.transformer.CoverModeTransformer
import com.morteshka.transformer.ScaleYTransformer
import java.lang.reflect.Field

/**
 * Created by zhouwei on 17/5/26.
 */
class MZBannerView<T> : RelativeLayout {
    private var mViewPager: CustomViewPager? = null
    private var mAdapter: MZPagerAdapter<T>? = null
    private var mDatas: ArrayList<T>? = null
    private var mIsAutoPlay = true // 是否自动播放
    private var mCurrentItem = 0 //当前位置
    private val mHandler = Handler()
    private var mDelayedTime = 3000 // Banner 切换时间间隔
    private val mViewPagerScroller: ViewPagerScroller by lazy { ViewPagerScroller(context) }//控制ViewPager滑动速度的Scroller
    private var mIsOpenMZEffect = true // 开启魅族Banner效果
    private var mIsCanLoop = true // 是否轮播图片
    private var indicatorContainer: LinearLayout? = null//indicator容器
    private val mIndicators = ArrayList<AppCompatImageView>()

    //mIndicatorRes[0] 为为选中，mIndicatorRes[1]为选中
    private val mIndicatorRes =
        intArrayOf(R.drawable.indicator_normal, R.drawable.indicator_selected)
    private var mIndicatorPaddingLeft = 0 // indicator 距离左边的距离
    private var mIndicatorPaddingRight = 0 //indicator 距离右边的距离
    private var mIndicatorPaddingTop = 0 //indicator 距离上边的距离
    private var mIndicatorPaddingBottom = 0 //indicator 距离下边的距离
    private var mMZModePadding = 0 //在仿魅族模式下，由于前后显示了上下一个页面的部分，因此需要计算这部分padding
    private var mIndicatorAlign = 1
    private var mOnPageChangeListener: OnPageChangeListener? = null
    var mBannerPageClickListener: BannerPageClickListener? = null

    enum class IndicatorAlign {
        LEFT,  //做对齐
        CENTER,  //居中对齐
        RIGHT //右对齐
    }

    /**
     * 中间Page是否覆盖两边，默认覆盖
     */
    private var mIsMiddlePageCover = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        readAttrs(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        readAttrs(context, attrs)
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        readAttrs(context, attrs)
        init()
    }

    private fun readAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MZBannerView)
        mIsOpenMZEffect = typedArray.getBoolean(R.styleable.MZBannerView_open_mz_mode, true)
        mIsMiddlePageCover = typedArray.getBoolean(R.styleable.MZBannerView_middle_page_cover, true)
        mIsCanLoop = typedArray.getBoolean(R.styleable.MZBannerView_canLoop, true)
        mIndicatorAlign = typedArray.getInt(
            R.styleable.MZBannerView_indicatorAlign,
            IndicatorAlign.CENTER.ordinal
        )
        mIndicatorPaddingLeft =
            typedArray.getDimensionPixelSize(R.styleable.MZBannerView_indicatorPaddingLeft, 0)
        mIndicatorPaddingRight =
            typedArray.getDimensionPixelSize(R.styleable.MZBannerView_indicatorPaddingRight, 0)
        mIndicatorPaddingTop =
            typedArray.getDimensionPixelSize(R.styleable.MZBannerView_indicatorPaddingTop, 0)
        mIndicatorPaddingBottom =
            typedArray.getDimensionPixelSize(R.styleable.MZBannerView_indicatorPaddingBottom, 0)
        typedArray.recycle()
    }

    private fun init() {
        val view: View = if (mIsOpenMZEffect) {
            LayoutInflater.from(context).inflate(R.layout.mz_banner_effect_layout, this, true)
        } else {
            LayoutInflater.from(context).inflate(R.layout.mz_banner_normal_layout, this, true)
        }
        indicatorContainer =
            view.findViewById<View>(R.id.banner_indicator_container) as LinearLayout
        mViewPager = view.findViewById<View>(R.id.mzbanner_vp) as CustomViewPager
        mViewPager?.offscreenPageLimit = 4
        mMZModePadding = dpToPx(30)
        // 初始化Scroller
        initViewPagerScroll()
        sureIndicatorPosition()
    }

    /**
     * 是否开启魅族模式
     */
    private fun setOpenMZEffect() {
        // 魅族模式
        if (mIsOpenMZEffect) {
            if (mIsMiddlePageCover) {
                // 中间页面覆盖两边，和魅族APP 的banner 效果一样。
                mViewPager?.setPageTransformer(true, CoverModeTransformer(mViewPager))
            } else {
                // 中间页面不覆盖，页面并排，只是Y轴缩小
                mViewPager?.setPageTransformer(false, ScaleYTransformer())
            }
        }
    }

    /**
     * make sure the indicator
     */
    private fun sureIndicatorPosition() {
        when (mIndicatorAlign) {
            IndicatorAlign.LEFT.ordinal -> {
                setIndicatorAlign(IndicatorAlign.LEFT)
            }
            IndicatorAlign.CENTER.ordinal -> {
                setIndicatorAlign(IndicatorAlign.CENTER)
            }
            else -> {
                setIndicatorAlign(IndicatorAlign.RIGHT)
            }
        }
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private fun initViewPagerScroll() {
        try {
            val mScroller: Field? = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller?.isAccessible = true
            mScroller?.set(mViewPager, mViewPagerScroller)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private val mLoopRunnable: Runnable by lazy {
        object : Runnable {
            override fun run() {
                if (mIsAutoPlay) {
                    mCurrentItem = mViewPager!!.currentItem
                    mCurrentItem++
                    if (mCurrentItem == mAdapter!!.count - 1) {
                        mCurrentItem = 0
                        mViewPager?.setCurrentItem(mCurrentItem, false)
                        mHandler.postDelayed(this, mDelayedTime.toLong())
                    } else {
                        mViewPager?.currentItem = mCurrentItem
                        mHandler.postDelayed(this, mDelayedTime.toLong())
                    }
                } else {
                    mHandler.postDelayed(this, mDelayedTime.toLong())
                }
            }
        }
    }

    /**
     * 初始化指示器Indicator
     */
    private fun initIndicator() {
        indicatorContainer!!.removeAllViews()
        mIndicators.clear()
        if (!mDatas.isNullOrEmpty()) {
            for (i in mDatas!!.indices) {
                val imageView = AppCompatImageView(context)
                if (mIndicatorAlign == IndicatorAlign.LEFT.ordinal) {
                    if (i == 0) {
                        val paddingLeft =
                            if (mIsOpenMZEffect) mIndicatorPaddingLeft + mMZModePadding else mIndicatorPaddingLeft
                        imageView.setPadding(paddingLeft + 6, 0, 6, 0)
                    } else {
                        imageView.setPadding(6, 0, 6, 0)
                    }
                } else if (mIndicatorAlign == IndicatorAlign.RIGHT.ordinal) {
                    if (i == mDatas!!.size - 1) {
                        val paddingRight =
                            if (mIsOpenMZEffect) mMZModePadding + mIndicatorPaddingRight else mIndicatorPaddingRight
                        imageView.setPadding(6, 0, 6 + paddingRight, 0)
                    } else {
                        imageView.setPadding(6, 0, 6, 0)
                    }
                } else {
                    imageView.setPadding(6, 0, 6, 0)
                }
                if (i == mCurrentItem % mDatas!!.size) {
                    imageView.setImageResource(mIndicatorRes[1])
                } else {
                    imageView.setImageResource(mIndicatorRes[0])
                }
                mIndicators.add(imageView)
                indicatorContainer?.addView(imageView)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!mIsCanLoop) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_DOWN -> {
                val paddingLeft = mViewPager!!.left
                val touchX = ev.rawX
                // 如果是魅族模式，去除两边的区域
                if (touchX >= paddingLeft && touchX < getScreenWidth(context) - paddingLeft) {
                    pause()
                }
            }
            MotionEvent.ACTION_UP -> start()
        }
        return super.dispatchTouchEvent(ev)
    }
    /** */
    /**                             对外API                                                                */
    /** */
    /**
     * 开始轮播
     *
     * 应该确保在调用用了[{][.setPages] 之后调用这个方法开始轮播
     */
    fun start() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (mAdapter == null) {
            return
        }
        if (mIsCanLoop) {
            pause()
            mIsAutoPlay = true
            mHandler.postDelayed(mLoopRunnable, mDelayedTime.toLong())
        }
    }

    /**
     * 停止轮播
     */
    fun pause() {
        mIsAutoPlay = false
        mHandler.removeCallbacks(mLoopRunnable)
    }

    /**
     * 设置是否可以轮播
     *
     * @param canLoop
     */
    fun setCanLoop(canLoop: Boolean) {
        mIsCanLoop = canLoop
        if (!canLoop) {
            pause()
        }
    }

    /**
     * 设置BannerView 的切换时间间隔
     *
     * @param delayedTime
     */
    fun setDelayedTime(delayedTime: Int) {
        mDelayedTime = delayedTime
    }

    fun addPageChangeListener(onPageChangeListener: OnPageChangeListener?) {
        mOnPageChangeListener = onPageChangeListener
    }

    /**
     * 是否显示Indicator
     *
     * @param visible true 显示Indicator，否则不显示
     */
    fun setIndicatorVisible(visible: Boolean) {
        indicatorContainer?.visibility = if (visible) {
            VISIBLE
        } else {
            GONE
        }
    }

    /**
     * set indicator padding
     *
     * @param paddingLeft
     * @param paddingTop
     * @param paddingRight
     * @param paddingBottom
     */
    fun setIndicatorPadding(
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        mIndicatorPaddingLeft = paddingLeft
        mIndicatorPaddingTop = paddingTop
        mIndicatorPaddingRight = paddingRight
        mIndicatorPaddingBottom = paddingBottom
        sureIndicatorPosition()
    }

    /**
     * 返回ViewPager
     *
     * @return [ViewPager]
     */
    val viewPager: ViewPager?
        get() = mViewPager

    /**
     * 设置indicator 图片资源
     *
     * @param unSelectRes 未选中状态资源图片
     * @param selectRes   选中状态资源图片
     */
    fun setIndicatorRes(@DrawableRes unSelectRes: Int, @DrawableRes selectRes: Int) {
        mIndicatorRes[0] = unSelectRes
        mIndicatorRes[1] = selectRes
    }

    /**
     * 设置数据，这是最重要的一个方法。
     *
     * 其他的配置应该在这个方法之前调用
     *
     * @param datas           Banner 展示的数据集合
     * @param mzHolderCreator ViewHolder生成器 [MZHolderCreator] And [MZViewHolder]
     */
    fun setPages(datas: ArrayList<T>, mzHolderCreator: MZHolderCreator<*>?) {
        if (mzHolderCreator == null) {
            return
        }
        mDatas = datas
        //如果在播放，就先让播放停止
        pause()

        //增加一个逻辑：由于魅族模式会在一个页面展示前后页面的部分，因此，数据集合的长度至少为3,否则，自动为普通Banner模式
        //不管配置的:open_mz_mode 属性的值是否为true
        if (datas.size < 3) {
            mIsOpenMZEffect = false
            val layoutParams = mViewPager!!.layoutParams as MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            mViewPager?.layoutParams = layoutParams
            clipChildren = true
            mViewPager?.clipChildren = true
        }
        setOpenMZEffect()
        // 2017.7.20 fix：将Indicator初始化放在Adapter的初始化之前，解决更新数据变化更新时crush.
        //初始化Indicator
        initIndicator()
        mAdapter = MZPagerAdapter(datas, mzHolderCreator, mIsCanLoop)
        mAdapter?.setUpViewViewPager(mViewPager)
        mAdapter?.mPageClickListener = mBannerPageClickListener
        mViewPager?.clearOnPageChangeListeners()
        mViewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val realPosition = position % mIndicators.size
                mOnPageChangeListener?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                mCurrentItem = position
                mDatas?.let {
                    // 切换indicator
                    val realSelectPosition = mCurrentItem % mIndicators.size
                    for (i in it.indices) {
                        mIndicators[i].setImageResource(
                            if (i == realSelectPosition) {
                                mIndicatorRes[1]
                            } else {
                                mIndicatorRes[0]
                            }
                        )

                    }
                    // 不能直接将mOnPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的position
                    mOnPageChangeListener?.onPageSelected(realSelectPosition)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> mIsAutoPlay = false
                    ViewPager.SCROLL_STATE_SETTLING -> mIsAutoPlay = true
                }
                mOnPageChangeListener?.onPageScrollStateChanged(state)
            }
        })
    }

    /**
     * 设置Indicator 的对齐方式
     *
     * @param indicatorAlign [IndicatorAlign.CENTER][IndicatorAlign.LEFT][IndicatorAlign.RIGHT]
     */
    fun setIndicatorAlign(indicatorAlign: IndicatorAlign) {
        mIndicatorAlign = indicatorAlign.ordinal
        val layoutParams = indicatorContainer!!.layoutParams as LayoutParams
        when (indicatorAlign) {
            IndicatorAlign.LEFT -> {
                layoutParams.addRule(ALIGN_PARENT_LEFT)
            }
            IndicatorAlign.RIGHT -> {
                layoutParams.addRule(ALIGN_PARENT_RIGHT)
            }
            else -> {
                layoutParams.addRule(CENTER_HORIZONTAL)
            }
        }

        // 2017.8.27 添加：增加设置Indicator 的上下边距。
        layoutParams.setMargins(0, mIndicatorPaddingTop, 0, mIndicatorPaddingBottom)
        indicatorContainer?.layoutParams = layoutParams
    }

    /**
     * 设置是否使用ViewPager默认是的切换速度
     *
     * @param useDefaultDuration 切换动画时间
     */
    fun setUseDefaultDuration(useDefaultDuration: Boolean) {
        mViewPagerScroller.isUseDefaultDuration = useDefaultDuration
    }
    /**
     * 获取Banner页面切换动画时间
     *
     * @return
     */
    /**
     * 设置ViewPager切换的速度
     *
     * @param duration 切换动画时间
     */
    var duration: Int
        get() = mViewPagerScroller.scrollDuration
        set(duration) {
            mViewPagerScroller.duration = duration
        }


    companion object {
        fun getScreenWidth(context: Context): Int {
            val resources = context.resources
            val dm = resources.displayMetrics
            return dm.widthPixels
        }

        fun dpToPx(dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                Resources.getSystem().displayMetrics
            ).toInt()
        }
    }
}