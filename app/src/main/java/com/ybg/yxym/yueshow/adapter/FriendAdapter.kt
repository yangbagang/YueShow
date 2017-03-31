package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ybg.yxym.yb.bean.UserMsg
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2017/3/31.
 */
class FriendAdapter(private val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater

    private var msgList: List<UserMsg>? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    fun setData(msgList: List<UserMsg>) {
        this.msgList = msgList
    }

    override fun getCount(): Int {
        return msgList?.size ?: 0
    }

    override fun getItem(position: Int): UserMsg? {
        return if (msgList != null) msgList!![position] else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var viewholder: ViewHolder? = null
        val userMsg = getItem(position)
        if (view == null) {
            viewholder = ViewHolder()
            view = inflater.inflate(R.layout.list_item_friend_2, null)
            viewholder.tv_msg = view
                    .findViewById(R.id.tv_friend_msg) as TextView
            viewholder.tv_name = view
                    .findViewById(R.id.tv_friend_name) as TextView
            viewholder.tv_time = view
                    .findViewById(R.id.tv_friend_time) as TextView
            viewholder.ci_contact = view
                    .findViewById(R.id.ci_friend_avatar) as CircleImageView
            view.tag = viewholder
        } else {
            viewholder = view.tag as ViewHolder
        }
        if (userMsg != null) {// 获取首字母的assii值
            viewholder.tv_msg!!.text = userMsg.msg
            viewholder.tv_time!!.text = DateUtil.getTimeInterval(userMsg.time)
            viewholder.tv_name!!.text = userMsg.userBase.nickName
            //头像
            val avatar = HttpUrl.getImageUrl(userMsg.userBase.avatar)
            ImageLoaderUtils.instance.loadBitmap(viewholder.ci_contact!!, avatar)
        }
        return view!!
    }

    internal inner class ViewHolder {
        var tv_msg: TextView? = null
        var tv_name: TextView? = null
        var tv_time: TextView? = null
        var ci_contact: CircleImageView? = null
    }

}