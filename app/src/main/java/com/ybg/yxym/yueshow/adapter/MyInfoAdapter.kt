package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.ybg.yxym.yueshow.R

import java.util.ArrayList

/**
 * 类描述：个人资料适配器
 */
class MyInfoAdapter(private val mContext: Context) : BaseAdapter() {
    private var stringList: List<String>? = null
    private val itemNames = arrayOf("悦美号", "昵称:", "生日:", "性别:", "职业:", "身高:", "体重:", "身材:", "地区:", "个性签名:")


    init {
        stringList = ArrayList<String>()
    }

    override fun getCount(): Int {
        return itemNames.size
    }

    override fun getItem(position: Int): Any {
        return itemNames[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_info_list, null)
            viewHolder = ViewHolder()
            viewHolder.tv_title = convertView!!.findViewById(R.id.tv_title) as TextView
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name) as TextView
            viewHolder.iv_arrow = convertView.findViewById(R.id.iv_arrow) as ImageView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.tv_title!!.text = itemNames[position]
        viewHolder.tv_name!!.text = stringList!![position]

        if (position == 0 || position == 3) {
            viewHolder.iv_arrow!!.visibility = View.GONE
        }

        return convertView
    }


    internal inner class ViewHolder {
        var tv_title: TextView? = null
        var tv_name: TextView? = null
        var iv_arrow: ImageView? = null
    }

    fun setData(list: List<String>) {
        this.stringList = list
    }
}
