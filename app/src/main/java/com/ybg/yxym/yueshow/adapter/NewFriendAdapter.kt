package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.ybg.yxym.yb.bean.FriendRequest
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2017/3/31.
 */
class NewFriendAdapter(private val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater

    private var requestList: List<FriendRequest>? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    fun setData(msgList: List<FriendRequest>) {
        this.requestList = msgList
    }

    override fun getCount(): Int {
        return requestList?.size ?: 0
    }

    override fun getItem(position: Int): FriendRequest? {
        return if (requestList != null) requestList!![position] else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var viewholder: ViewHolder? = null
        val request = getItem(position)
        if (view == null) {
            viewholder = ViewHolder()
            view = inflater.inflate(R.layout.list_item_new_friend, null)
            viewholder.tv_msg = view
                    .findViewById(R.id.tv_friend_msg) as TextView
            viewholder.tv_name = view
                    .findViewById(R.id.tv_friend_name) as TextView
            viewholder.tv_status = view
                    .findViewById(R.id.tv_friend_time) as TextView
            viewholder.tv_status = view
                    .findViewById(R.id.tv_friend_time) as TextView
            viewholder.ci_avatar = view
                    .findViewById(R.id.ci_friend_avatar) as CircleImageView
            view.tag = viewholder
        } else {
            viewholder = view.tag as ViewHolder
        }
        if (request != null) {
            viewholder.tv_msg!!.text = request.reason
            viewholder.tv_name!!.text = request.fromUser.nickName
            //头像
            val avatar = HttpUrl.getImageUrl(request.fromUser.avatar)
            ImageLoaderUtils.instance.loadBitmap(viewholder.ci_avatar!!, avatar)
            when (request.status) {
                0 -> {
                    viewholder.btn_status!!.visibility = View.VISIBLE
                    viewholder.tv_status!!.visibility = View.GONE
                }
                1 -> {
                    viewholder.btn_status!!.visibility = View.GONE
                    viewholder.tv_status!!.visibility = View.VISIBLE
                    viewholder.tv_status!!.text = "己添加"
                }
                2 -> {
                    viewholder.btn_status!!.visibility = View.GONE
                    viewholder.tv_status!!.visibility = View.VISIBLE
                    viewholder.tv_status!!.text = "己拒绝"
                }
            }
            viewholder.btn_status!!.setOnClickListener {
                val showApplication = context.applicationContext as ShowApplication
                SendRequest.acceptFriendRequest(context, showApplication.token, "${request.id}",
                        object : OkCallback<String>(OkStringParser()) {
                            override fun onSuccess(code: Int, response: String) {
                                val jsonBean = JSonResultBean.fromJSON(response)
                                if (jsonBean != null && jsonBean.isSuccess) {
                                    ToastUtil.show("操作完成")
                                    viewholder!!.btn_status!!.visibility = View.GONE
                                    viewholder!!.tv_status!!.visibility = View.VISIBLE
                                    viewholder!!.tv_status!!.text = "己添加"
                                } else {
                                    jsonBean.let {
                                        ToastUtil.show(jsonBean?.message ?: "操作失败")
                                    }
                                }
                            }

                            override fun onFailure(e: Throwable) {
                                ToastUtil.show("操作失败")
                            }
                        })
            }
        }
        return view!!
    }

    internal inner class ViewHolder {
        var tv_msg: TextView? = null
        var tv_name: TextView? = null
        var tv_status: TextView? = null
        val btn_status: Button? = null
        var ci_avatar: CircleImageView? = null
    }

}