package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.ybg.yxym.yb.bean.SystemLabel
import com.ybg.yxym.yueshow.R

import java.util.ArrayList

class LabelAdapter(private val mContext: Context) : BaseAdapter() {
    private var stringList: List<SystemLabel>? = null

    private val BULE = 0xff55c3fc.toInt()
    private val WHITE = 0xffffffff.toInt()

    init {
        stringList = ArrayList<SystemLabel>()
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_interesting, null)
            viewHolder = ViewHolder()
            viewHolder.tv_biaoqian = convertView!!.findViewById(R.id.tv_biaoqian) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.tv_biaoqian!!.text = stringList!![position].label
        if (stringList!![position].isSelected) {//被选中
            viewHolder.tv_biaoqian!!.setTextColor(WHITE)
            viewHolder.tv_biaoqian!!.background = mContext.resources.getDrawable(R.drawable.shape_biaoqian_blue_bg)
        } else {//没被选中
            viewHolder.tv_biaoqian!!.setTextColor(BULE)
            viewHolder.tv_biaoqian!!.background = mContext.resources.getDrawable(R.drawable.shape_biaoqian_blue_stoke_bg)
        }

        return convertView
    }


    internal inner class ViewHolder {
        var tv_biaoqian: TextView? = null
    }

    /**
     * @param stringList
     */
    fun setData(stringList: List<SystemLabel>) {
        this.stringList = stringList
    }
}
