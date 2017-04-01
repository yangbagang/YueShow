package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.FriendRequest
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.NewFriendAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_new_friend.*
import java.util.*

class NewFriendActivity : BaseActivity() {

    private var requestList: MutableList<FriendRequest> = ArrayList<FriendRequest>()
    private lateinit var requestAdapter: NewFriendAdapter

    override fun setContentViewId(): Int {
        return R.layout.activity_new_friend
    }

    override fun setUpView() {
        setCustomTitle("添加朋友")
    }

    override fun init() {
        requestAdapter = NewFriendAdapter(mContext!!)
        lv_friend_list.adapter = requestAdapter
        getFriendMsgList()
    }

    private fun getFriendMsgList() {
        SendRequest.getFriendRequestList(mContext!!, mApplication.token, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val data = mGson!!.fromJson<List<FriendRequest>>(jsonBean.data, object :
                            TypeToken<List<FriendRequest>>() {}.type)
                    requestList.clear()
                    if (data != null && data.isNotEmpty()) {
                        requestList.addAll(data)
                        requestAdapter.setData(requestList)
                        requestAdapter.notifyDataSetChanged()
                    }
                } else {
                    jsonBean.let {
                        ToastUtil.show(jsonBean?.message ?: "获取列表失败")
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取列表失败")
            }
        })
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, NewFriendActivity::class.java))
        }
    }
}
