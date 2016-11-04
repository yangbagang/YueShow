package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.ybg.yxym.yueshow.R

import java.util.ArrayList

class PlaceAdapter(private val mContext: Context) : BaseAdapter() {
    private var stringList: List<String>? = null

    init {
        stringList = ArrayList<String>()
    }

    override fun getCount(): Int {
        return stringList!!.size
    }

    override fun getItem(position: Int): Any {
        return stringList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_select_place, null)
            viewHolder = ViewHolder()
            viewHolder.tv_title = convertView!!.findViewById(R.id.tv_title) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.tv_title!!.text = stringList!![position]
        return convertView
    }


    internal inner class ViewHolder {
        var tv_title: TextView? = null
    }

    /**
     * @param list
     */
    fun setData(list: List<String>) {
        this.stringList = list
    }
}
