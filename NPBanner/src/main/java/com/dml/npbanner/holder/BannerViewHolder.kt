package com.dml.npbanner.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dml.npbanner.R
import com.squareup.picasso.Picasso

class BannerViewHolder<T> : NPViewHolder<T> {
    private var mImageView: AppCompatImageView? = null

    override fun createView(context: Context?): View {

        // 返回页面布局文件
        val view: View = LayoutInflater.from(context).inflate(R.layout.banner_item, null)
        mImageView = view.findViewById(R.id.bannerImage)
        return view
    }

    override fun onBind(context: Context?, position: Int, data: T) {
        if (data is Int) {
            // 数据绑定
            mImageView?.setImageResource(data)
        } else if (data is String) {
            Picasso.get().load(data).into(mImageView)
        }
    }
}