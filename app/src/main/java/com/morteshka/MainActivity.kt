package com.morteshka

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.morteshka.holder.BannerPageClickListener
import com.morteshka.holder.BannerViewHolder
import com.morteshka.mooseman.coustombanner.R

class MainActivity : AppCompatActivity() {
    var banner: MZBannerView<Int>? = null
    var bannerNormal: MZBannerView<Int>? = null
    private val TAG = "MZModeBannerFragment"
    private val RES = intArrayOf(
        R.drawable.image5,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        R.drawable.image6,
        R.drawable.image7,
        R.drawable.image8
    )
    private val BANNER = intArrayOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.banner5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }


    private fun initView() {
        banner = findViewById(R.id.banner)
        bannerNormal = findViewById(R.id.bannerNormal)
        banner?.mBannerPageClickListener = object : BannerPageClickListener {
            override fun onPageClick(view: View?, position: Int) {

            }
        }
        banner?.addPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                Log.e(TAG, "addPageChangeLisnter:$position")
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        val list = ArrayList<Int>()
        for (i in RES.indices) {
            list.add(RES[i])
        }
        val bannerList = ArrayList<Int>()
        for (i in BANNER.indices) {
            bannerList.add(BANNER[i])
        }
        banner?.setIndicatorVisible(true)
        // 代码中更改indicator 的位置
        //mMZBanner.setIndicatorAlign(MZBannerView.IndicatorAlign.LEFT);
        //mMZBanner.setIndicatorPadding(10,0,0,150);
        banner?.setPages(
            bannerList
        ) { BannerViewHolder<Int>() }
        bannerNormal?.setPages(
            list
        ) { BannerViewHolder<Int>() }
    }

    override fun onPause() {
        super.onPause()
        banner?.pause()
        bannerNormal?.pause()
    }

    override fun onResume() {
        super.onResume()
        banner?.start()
        bannerNormal?.start()
    }
}