package com.morteshka.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.morteshka.mooseman.coustombanner.R

class BannerViewHolder<T> : MZViewHolder<T> {
    private var mImageView: AppCompatImageView? = null
    override fun createView(context: Context?): View {
        // 返回页面布局文件
        val view: View = LayoutInflater.from(context).inflate(R.layout.banner_item, null)
        mImageView = view.findViewById(R.id.banner_image)
        return view
    }

    override fun onBind(context: Context?, position: Int, data: T) {
        if (data is Int) {
            // 数据绑定
            mImageView?.setImageResource(data)
        }
    }
}