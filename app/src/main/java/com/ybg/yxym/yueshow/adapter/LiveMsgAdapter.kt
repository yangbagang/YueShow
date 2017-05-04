package com.ybg.yxym.yueshow.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ybg.yxym.yb.bean.LiveMsg
import com.ybg.yxym.yb.utils.MeiLiUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.MeiLiImgUtil
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2017/1/26.
 */
class LiveMsgAdapter(private var mContext: Activity): BaseAdapter() {

    private var mList: List<LiveMsg>? = null
    private var inflater: LayoutInflater? = null

    fun setDataList(list: List<LiveMsg>) {
        mList = list
    }

    override fun getCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): LiveMsg {
        return mList!![position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        //初始化定义
        var convertView = view
        var viewHolder: ViewHolder? = null
        val msg = mList!![position]
        if (msg.type == 1) {//送礼
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.list_item_live_msg_1, parent, false)
            viewHolder = ViewHolder()
            initViewHolder(viewHolder, convertView)
            //头像
            val avatar = HttpUrl.getImageUrl(msg.userBase.avatar)
            ImageLoaderUtils.instance.loadBitmap(viewHolder.userAvatar!!, avatar)
            setGiftMsg(viewHolder, msg.msg)
        } else if (msg.type == 2) {//发消息
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.list_item_live_msg_2, parent, false)
            viewHolder = ViewHolder()
            initViewHolder(viewHolder, convertView)
            val levelNum = MeiLiUtil.getLevelNum(msg.userBase.score)
            viewHolder.userLevelNum?.text = "LV$levelNum"
            val levelImg = MeiLiImgUtil.getImgId(levelNum)
            ImageLoaderUtils.instance.loadBitmap(viewHolder.userLevelImg!!, levelImg)
            //消息
            viewHolder.msgText?.text = msg.msg
        } else {
            viewHolder = convertView?.tag as ViewHolder
        }

        //名称
        viewHolder.userName?.text = msg.userBase.nickName

        return convertView!!
    }

    private fun setGiftMsg(viewHolder: ViewHolder, giftMsg: String) {
        //实现礼物消息内容及图片
        val info = giftMsg.split(",")
        viewHolder.msgText?.text = info[0]
        ImageLoaderUtils.instance.loadBitmap(viewHolder.liveGift!!, HttpUrl.getImageUrl(info[1]))
    }

    private fun initViewHolder(viewHolder: ViewHolder, convertView: View) {
        viewHolder.userName = convertView.findViewById(R.id.userName) as TextView?
        viewHolder.userAvatar = convertView.findViewById(R.id.userAvatar) as CircleImageView?
        viewHolder.msgText = convertView.findViewById(R.id.msgText) as TextView?
        viewHolder.liveGift = convertView.findViewById(R.id.liveGift) as ImageView?
        viewHolder.userLevelNum = convertView.findViewById(R.id.userLevelNum) as TextView?
        viewHolder.userLevelImg = convertView.findViewById(R.id.userLevelImg) as ImageView?

        convertView.tag = viewHolder
    }

    inner class ViewHolder {
        internal var userName: TextView? = null
        internal var userAvatar: CircleImageView? = null
        internal var msgText: TextView? = null
        internal var liveGift: ImageView? = null
        internal var userLevelNum: TextView? = null
        internal var userLevelImg: ImageView? = null
    }

}