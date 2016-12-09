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
class BangAdapter(private var mContext: Activity): BaseAdapter() {

    private var mList: List<BangItem>? = null
    private var inflater: LayoutInflater? = null
    private var scoreName = ""

    fun setDataList(list: List<BangItem>) {
        mList = list
    }

    fun setScoreName(name: String) {
        this.scoreName = name
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
        if (convertView == null) {
            inflater = LayoutInflater.from(mContext)
            convertView = inflater!!.inflate(R.layout.list_item_bang, parent, false)
            viewHolder = ViewHolder()
            initViewHolder(viewHolder, convertView)
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //名次
        if (position > 2) {
            viewHolder.iv_pm!!.visibility = View.GONE
            viewHolder.tv_pm!!.visibility = View.VISIBLE
            viewHolder.tv_pm!!.text = "$position"
        } else {
            viewHolder.iv_pm!!.visibility = View.VISIBLE
            viewHolder.tv_pm!!.visibility = View.GONE
            //前三名
            if (position == 0) {
                viewHolder.iv_pm!!.setImageResource(R.mipmap.pm1)
            } else if (position == 1) {
                viewHolder.iv_pm!!.setImageResource(R.mipmap.pm2)
            } else if (position == 2) {
                viewHolder.iv_pm!!.setImageResource(R.mipmap.pm3)
            }
        }

        val bangItem = getItem(position)
        //头像
        val avatar = HttpUrl.getImageUrl(bangItem.avatar)
        ImageLoaderUtils.instance.loadBitmap(viewHolder.ci_pm!!, avatar)
        //积分名称及积分值
        viewHolder.tv_score_name!!.text = scoreName
        viewHolder.tv_pm_score!!.text = "${bangItem.scoreValue}"

        //加载蜜爱
        loadMiAi(bangItem.user_id, viewHolder)

        return convertView!!
    }

    private fun loadMiAi(userId: Long, viewHolder: ViewHolder) {
        SendRequest.getMiAiBang(mContext, "2016-01-01", "2999-12-31", 1, 3, userId, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val list = GsonUtil.createGson().fromJson<List<BangItem>>(jsonBean.data, object :
                            TypeToken<List<BangItem>>(){}.type)
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            ImageLoaderUtils.instance.loadBitmap(viewHolder.iv_mi_0!!, HttpUrl.getImageUrl
                            (first.avatar))
                        } else {
                            viewHolder.iv_mi_0!!.visibility = View.GONE
                        }
                        if (list.size > 1) {
                            val second = list[1]
                            ImageLoaderUtils.instance.loadBitmap(viewHolder.iv_mi_1!!, HttpUrl.getImageUrl
                            (second.avatar))
                        } else {
                            viewHolder.iv_mi_1!!.visibility = View.GONE
                        }
                        if (list.size > 2) {
                            val third = list[2]
                            ImageLoaderUtils.instance.loadBitmap(viewHolder.iv_mi_2!!, HttpUrl.getImageUrl
                            (third.avatar))
                        } else {
                            viewHolder.iv_mi_2!!.visibility = View.GONE
                        }
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                //ToastUtil.show("获取美力榜失败")
            }

        })
    }

    private fun initViewHolder(viewHolder: ViewHolder, convertView: View) {
        viewHolder.iv_pm = convertView.findViewById(R.id.iv_pm) as ImageView?
        viewHolder.tv_pm = convertView.findViewById(R.id.tv_pm) as TextView?
        viewHolder.ci_pm = convertView.findViewById(R.id.ci_pm) as CircleImageView?
        viewHolder.tv_pm_name = convertView.findViewById(R.id.tv_pm_name) as TextView?
        viewHolder.tv_score_name = convertView.findViewById(R.id.tv_score_name) as TextView?
        viewHolder.tv_pm_score = convertView.findViewById(R.id.tv_pm_score) as TextView?

        viewHolder.iv_mi_0 = convertView.findViewById(R.id.iv_mi_0) as ImageView?
        viewHolder.iv_mi_1 = convertView.findViewById(R.id.iv_mi_1) as ImageView?
        viewHolder.iv_mi_2 = convertView.findViewById(R.id.iv_mi_2) as ImageView?

        convertView.tag = viewHolder
    }

    inner class ViewHolder {
        internal var iv_pm: ImageView? = null
        internal var tv_pm: TextView? = null
        internal var ci_pm: CircleImageView? = null
        internal var tv_pm_name: TextView? = null
        internal var tv_score_name: TextView? = null
        internal var tv_pm_score: TextView? = null
        internal var iv_mi_0: ImageView? = null
        internal var iv_mi_1: ImageView? = null
        internal var iv_mi_2: ImageView? = null
    }
}