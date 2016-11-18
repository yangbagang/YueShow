package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.graphics.Bitmap
import android.widget.TextView
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.photo.FilterEffect
import com.ybg.yxym.yueshow.gpuimage.GPUImageView
import com.ybg.yxym.yueshow.gpuimage.util.GPUImageFilterTools

class FilterAdapter : RecyclerBaseAdapter<FilterEffect> {

    private var mBackground: Bitmap? = null
    private var m_ivGPUImageView: GPUImageView? = null
    private var m_tvFilterName: TextView? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, background: Bitmap) : super(context) {
        mBackground = background
    }

    override val rootResource: Int
        get() = R.layout.item_bottom_filter

    override fun getView(viewHolder: RecyclerBaseAdapter<FilterEffect>.BaseViewHolder, item: FilterEffect?, position: Int) {
        m_ivGPUImageView = viewHolder.getView(R.id.small_filter)
        m_tvFilterName = viewHolder.getView(R.id.filter_name)
        if (item != null) {
            m_tvFilterName!!.text = item.title
            val filter = GPUImageFilterTools.createFilterForType(mContext, item.type)
            m_ivGPUImageView!!.setImage(mBackground)
            m_ivGPUImageView!!.filter = filter

        }
        viewHolder.setIsRecyclable(false)
    }
}
