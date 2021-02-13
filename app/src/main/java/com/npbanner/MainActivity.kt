package com.npbanner

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bartoszlipinski.flippablestackview.StackPageTransformer
import com.dml.npbanner.holder.BannerPageClickListener
import com.dml.npbanner.holder.BannerViewHolder
import com.dml.npbanner.otherpart.BlankFragment
import com.dml.npbanner.otherpart.ColorFragmentAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.npbanner.mooseman.coustombanner.R
import com.npbanner.mooseman.coustombanner.databinding.ActivityMainBinding
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mViewPagerFragments: ArrayList<Fragment> = ArrayList<Fragment>()

    private val BANNER = arrayListOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.banner5
    )

    private val bannerUrl = arrayListOf(
        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=901485956,174296930&fm=26&gp=0.jpg",
        "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2194917290,1377555078&fm=26&gp=0.jpg",
        "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=148376656,4197264461&fm=26&gp=0.jpg",
        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3046665098,1325252244&fm=26&gp=0.jpg",
        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2208410246,2960134560&fm=26&gp=0.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
    }


    private fun initView() {
        binding.banner.mBannerPageClickListener = object : BannerPageClickListener {
            override fun onPageClick(view: View?, position: Int) {

            }
        }
        binding.banner.addPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                Log.e("TAG", "addPageChangeLisnter:$position")
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        // 代码中更改indicator 的位置
        //mMZBanner.setIndicatorAlign(MZBannerView.IndicatorAlign.LEFT);
        //mMZBanner.setIndicatorPadding(10,0,0,150);
        binding.banner.setPages(BANNER, BannerViewHolder<Int>())
        binding.bannerScale.setPages(BANNER, BannerViewHolder<Int>())
        binding.bannerNormal.setPages(bannerUrl, BannerViewHolder<String>())
        binding.horizotal.setPages(bannerUrl, BannerViewHolder<String>())
        binding.depth.setPages(bannerUrl, BannerViewHolder<String>())
        binding.zoomOut.setPages(bannerUrl, BannerViewHolder<String>())
        binding.scaleX.setPages(bannerUrl, BannerViewHolder<String>())
        binding. wordRecycler.initStack(3, StackPageTransformer.Orientation.HORIZONTAL)
        mViewPagerFragments.add(BlankFragment())
        mViewPagerFragments.add(BlankFragment())
        binding.wordRecycler.adapter= ColorFragmentAdapter(supportFragmentManager,mViewPagerFragments)
    }

    override fun onPause() {
        super.onPause()
        binding.banner.pause()
        binding.bannerNormal.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.banner.start()
        binding.bannerNormal.start()
    }
}