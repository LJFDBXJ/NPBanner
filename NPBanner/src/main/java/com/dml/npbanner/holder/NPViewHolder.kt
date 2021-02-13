package com.dml.npbanner.holder

import android.content.Context
import android.view.View

/**
 * Created by zhouwei on 17/5/26.
 */
interface NPViewHolder<T> {
    /**
     * 创建View
     *
     * @param context 上下文
     * @return 返回值
     */
    fun createView(context: Context?): View

    /**
     * 绑定数据
     *
     * @param context  上下文
     * @param position 位置
     * @param data     数据
     */
    fun onBind(context: Context?, position: Int, data: T)
}