package com.ybg.yxym.yueshow.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.BangItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.utils.GsonUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2016/12/4.
 */
class MiAiAdapter(private var mContext: Activity): BaseAdapter() {

    private var mList: List<BangItem>? = null
    private var inflater: LayoutInflater? = null

    fun setDataList(list: List<BangItem>) {
        mList = list
    }

    override fun getCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): BangItem {
        return mList!![position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        //初始化定义
        var convertView = view
        var viewHolder: ViewHolder? = null
        if (convertView == null && position < 3) {
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.list_item_mi_ai_1, parent, false)
            viewHolder = ViewHolder()
            initViewHolder(viewHolder, convertView)
        } else if (convertView == null) {
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.list_item_mi_ai_2, parent, false)
            viewHolder = ViewHolder()
            initViewHolder(viewHolder, convertView)
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //名次
        viewHolder.tv_pm?.text = "NO.${position + 1}"
        val bangItem = getItem(position)
        //头像
        val avatar = HttpUrl.getImageUrl(bangItem.avatar)
        ImageLoaderUtils.instance.loadBitmap(viewHolder.ci_pm!!, avatar)
        //积分名称及积分值
        viewHolder.tv_pm_name!!.text = "${bangItem.nickName}"
        viewHolder.tv_pm_score!!.text = "${bangItem.scoreValue}"

        return convertView!!
    }

    private fun initViewHolder(viewHolder: ViewHolder, convertView: View) {
        viewHolder.tv_pm = convertView.findViewById(R.id.tv_mi_ai_pm) as TextView?
        viewHolder.ci_pm = convertView.findViewById(R.id.ci_mi_ai_user) as CircleImageView?
        viewHolder.tv_pm_name = convertView.findViewById(R.id.tv_mi_ai_name) as TextView?
        viewHolder.tv_pm_score = convertView.findViewById(R.id.tv_mi_ai_score) as TextView?

        convertView.tag = viewHolder
    }

    inner class ViewHolder {
        internal var tv_pm: TextView? = null
        internal var ci_pm: CircleImageView? = null
        internal var tv_pm_name: TextView? = null
        internal var tv_pm_score: TextView? = null
    }
}