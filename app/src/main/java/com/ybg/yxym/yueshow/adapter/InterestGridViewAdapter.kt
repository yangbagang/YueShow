package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.ybg.yxym.yueshow.R

import java.util.ArrayList

class InterestGridViewAdapter(private val mContext: Context) : BaseAdapter() {
    private var stringList: List<String>? = null
    private var COUNT: Int = 0
    private val COLOR_GRAY = 0XFFD3D6E0.toInt()
    private val COLOR_WHITE = 0XFFFFFFFF.toInt()

    init {
        stringList = ArrayList<String>()
    }


    override fun getCount(): Int {
        return COUNT
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        if (position == COUNT) {
            return "+"
        }
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
        if (position == COUNT - 1) {
            viewHolder.tv_biaoqian!!.text = "+"
            viewHolder.tv_biaoqian!!.setTextColor(COLOR_GRAY)
            viewHolder.tv_biaoqian!!.background = mContext.resources.getDrawable(R.drawable.shape_biaoqian_white_stoke_bg)
        } else {
            viewHolder.tv_biaoqian!!.text = stringList!![position]
            viewHolder.tv_biaoqian!!.setTextColor(COLOR_WHITE)
            viewHolder.tv_biaoqian!!.background = mContext.resources.getDrawable(R.drawable.shape_biaoqian_blue_bg)
        }
        return convertView
    }


    internal inner class ViewHolder {
        var tv_biaoqian: TextView? = null
    }


    /**
     * @param stringList
     */
    fun setData(stringList: List<String>) {
        this.stringList = stringList
        this.COUNT = stringList.size + 1
    }
}
