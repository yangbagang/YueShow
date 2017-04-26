package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ybg.yxym.yb.bean.RuiGift
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import java.util.*

/**
 * Created by yangbagang on 2017/4/25.
 */
class GiftItemAdapter(private val mContext: Context) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    private var giftList: MutableList<RuiGift> = ArrayList<RuiGift>()

    fun setData(list: MutableList<RuiGift>) {
        giftList = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        var viewHolder: ViewHolder? = null
        val gift = getItem(position)
        if (view == null) {
            viewHolder = ViewHolder()
            view = inflater.inflate(R.layout.item_gift_info, null)
            viewHolder.tv_gift_price = view
                    .findViewById(R.id.tv_gift_price) as TextView
            viewHolder.tv_gift_ml = view
                    .findViewById(R.id.tv_gift_ml) as TextView
            viewHolder.iv_gift_img = view
                    .findViewById(R.id.iv_gift_img) as ImageView
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        if (gift != null) {
            viewHolder.tv_gift_price!!.text = "${gift.realPrice}"
            viewHolder.tv_gift_ml!!.text = "+ ${gift.realPrice * 4}"
            ImageLoaderUtils.instance.loadBitmap(viewHolder.iv_gift_img!!, HttpUrl.getImageUrl
            (gift.image))
        }
        return view!!
    }

    override fun getItem(position: Int): RuiGift {
        return giftList[position]
    }

    override fun getItemId(position: Int): Long {
        return giftList[position].id
    }

    override fun getCount(): Int {
        return giftList.size
    }

    /**
     * Viewholder
     */
    internal inner class ViewHolder {
        var iv_gift_img: ImageView? = null
        var tv_gift_price: TextView? = null
        var tv_gift_ml: TextView? = null
    }

}