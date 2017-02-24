package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader

import com.ybg.yxym.yueshow.R

/**
 * Created by yangbagang on 2016/11/16.
 */
class SelectedImageAdapter(context: Context) : RecyclerBaseAdapter<String>(context) {

    private var imgItem: ImageView? = null

    private var mBitmapList: MutableList<Bitmap?>? = null

    fun setBitmapList(bitmapList: MutableList<Bitmap?>) {
        mBitmapList = bitmapList
    }

    override val rootResource: Int
        get() = R.layout.item_top_image

    override fun getView(viewHolder: BaseViewHolder, item: String?, position: Int) {
        imgItem = viewHolder.getView(R.id.select_image)
        if (mBitmapList != null) {
            if (position < mBitmapList!!.size) {
                val bitmap = mBitmapList!![position]
                if (bitmap != null) {
                    imgItem?.setImageBitmap(bitmap)
                    return
                }
            }
        }
        if (item != null && imgItem != null) {
            ImageLoader.getInstance().displayImage(item, imgItem)
        }
    }

}