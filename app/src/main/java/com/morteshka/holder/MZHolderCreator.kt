package com.morteshka.holder

/**
 * Created by zhouwei on 17/5/26.
 */
interface MZHolderCreator<VH : MZViewHolder<VH>> {
    /**
     * 创建ViewHolder
     * @return
     */
    fun createViewHolder(): VH
}