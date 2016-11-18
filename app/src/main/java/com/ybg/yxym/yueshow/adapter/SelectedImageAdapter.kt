package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader

import com.ybg.yxym.yueshow.R

/**
 * Created by yangbagang on 2016/11/16.
 */
class SelectedImageAdapter :RecyclerBaseAdapter<String> {

    private var imgItem: ImageView? = null

    constructor(context: Context) : super(context) {
    }

    override val rootResource: Int
        get() = R.layout.item_top_image

    override fun getView(viewHolder: BaseViewHolder, item: String?, position: Int) {
        imgItem = viewHolder.getView(R.id.select_image)
        if (item != null && imgItem != null) {
            ImageLoader.getInstance().displayImage(item, imgItem)
        }
    }

}