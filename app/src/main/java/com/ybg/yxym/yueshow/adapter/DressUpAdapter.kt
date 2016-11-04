package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.picasso.Picasso

import java.util.ArrayList

class DressUpAdapter(private val mContext: Context) : BaseAdapter() {
    private var stringList: List<String>? = null

    private val COLOR_LV1_3 = 0XFFFFACFD.toInt()
    private val COLOR_LV4_6 = 0XFFFA5C0F.toInt()
    private val COLOR_LV7_9 = 0XFFDD4444.toInt()
    private val COLOR_LV10_12 = 0XFFDCB9FB.toInt()
    private val COLOR_LV13_15 = 0XFF58FB46.toInt()


    init {
        stringList = ArrayList<String>()
    }


    override fun getCount(): Int {
        return stringList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return stringList!![position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dress_up, null)
            viewHolder = ViewHolder()
            viewHolder.iv_wall = convertView!!.findViewById(R.id.iv_wall) as ImageView
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name) as TextView
            viewHolder.tv_level = convertView.findViewById(R.id.tv_level) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        if (position == 2 || position == 5) {
            viewHolder.tv_level!!.setBackgroundColor(COLOR_LV1_3)
            Picasso.with(mContext).load("http://dl.bizhi.sogou.com/images/2014/04/30/596279.jpg").resize(352, 276).centerCrop().into(viewHolder.iv_wall)
        } else if (position == 6 || position == 9) {
            viewHolder.tv_level!!.setBackgroundColor(COLOR_LV4_6)
            Picasso.with(mContext).load("http://ww2.sinaimg.cn/crop.0.0.1080.1080.1024/0064BfAujw8ewj6ch5ooaj30u00u0q56.jpg").resize(352, 276).centerCrop().into(viewHolder.iv_wall)
        } else if (position == 7 || position == 8) {
            viewHolder.tv_level!!.setBackgroundColor(COLOR_LV7_9)
            Picasso.with(mContext).load("http://ww2.sinaimg.cn/crop.0.0.1536.1536.1024/73213515jw8eswynq2pm8j216o16otce.jpg").resize(352, 276).centerCrop().into(viewHolder.iv_wall)
        } else if (position == 10 || position == 11) {
            viewHolder.tv_level!!.setBackgroundColor(COLOR_LV10_12)
            Picasso.with(mContext).load("http://img.taopic.com/uploads/allimg/140326/235113-1403260I33562.jpg").resize(352, 276).centerCrop().into(viewHolder.iv_wall)
        } else {
            viewHolder.tv_level!!.setBackgroundColor(COLOR_LV13_15)
            Picasso.with(mContext).load("http://img4.duitang.com/uploads/item/201407/16/20140716005719_VMF3d.jpeg").resize(352, 276).centerCrop().into(viewHolder.iv_wall)
        }


        return convertView
    }


    /**
     * Viewholder
     */
    internal inner class ViewHolder {
        var iv_wall: ImageView? = null
        var tv_name: TextView? = null
        var tv_level: TextView? = null
    }


    /**
     * @param stringList
     */
    fun setData(stringList: List<String>) {
        this.stringList = stringList
    }
}