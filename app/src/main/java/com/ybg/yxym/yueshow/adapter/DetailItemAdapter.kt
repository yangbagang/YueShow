package com.ybg.yxym.yueshow.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ybg.yxym.yueshow.R

import java.util.ArrayList

class DetailItemAdapter
/**
 * @param context 构造函数
 */
(private val mContext: Activity) : RecyclerView.Adapter<DetailItemAdapter.ViewHolder>() {
    private var mList: List<String> = ArrayList()

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailItemAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_home_list, parent, false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == mList.size - 1) {
            holder.vWhite.visibility = View.VISIBLE
        } else {
            holder.vWhite.visibility = View.GONE
        }
    }

    /**
     * ViewHolder
     */
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var tv_name: TextView
        val vWhite: View

        init {
            vWhite = v.findViewById(R.id.v_white) as View
            tv_name = v.findViewById(R.id.tv_name) as TextView
        }
    }


    /**
     * @param list
     */
    fun setData(list: List<String>) {
        this.mList = list
    }

}
