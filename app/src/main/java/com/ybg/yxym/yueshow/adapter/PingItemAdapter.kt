package com.ybg.yxym.yueshow.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ybg.yxym.yb.bean.ShowPing
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.view.CircleImageView
import java.util.*

/**
 * Created by yangbagang on 2016/12/25.
 */
class PingItemAdapter : RecyclerView.Adapter<PingItemAdapter.ViewHolder>() {

    private var mList: List<ShowPing> = ArrayList<ShowPing>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_show_user_ping, parent, false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val showPing = mList[position]

        holder?.let {
            holder.content.text = showPing.ping
            holder.time.text = DateUtil.getTimeInterval(showPing.createTime)
            holder.user.text = showPing.nickName
            ImageLoaderUtils.instance.loadBitmap(holder.avatar, HttpUrl.getImageUrl(showPing.avatar))
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val avatar: CircleImageView
        val user: TextView
        val time: TextView
        val content: TextView

        init {
            avatar = v.findViewById(R.id.ci_ping_user) as CircleImageView
            user = v.findViewById(R.id.tv_ping_user) as TextView
            time = v.findViewById(R.id.tv_ping_time) as TextView
            content = v.findViewById(R.id.tv_ping_content) as TextView
        }
    }

    fun setData(list: List<ShowPing>) {
        mList = list
    }
}