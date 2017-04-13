package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserMsg
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.FriendAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_friend2.*
import java.util.*

class FriendActivity2 : BaseActivity() {

    private var msgList: MutableList<UserMsg> = ArrayList<UserMsg>()
    private lateinit var msgAdapter: FriendAdapter

    override fun setContentViewId(): Int {
        return R.layout.activity_friend2
    }

    override fun setUpView() {
        setCustomTitle("朋友")
    }

    override fun init() {
        msgAdapter = FriendAdapter(mContext!!)
        lv_friend_list.adapter = msgAdapter
        getFriendMsgList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.friend_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_friend) {
            //跳转到消息页面
            ContactActivity.start(this@FriendActivity2)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getFriendMsgList() {
        SendRequest.getMsgList(mContext!!, mApplication.token, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val data = mGson!!.fromJson<List<UserMsg>>(jsonBean.data, object :
                            TypeToken<List<UserMsg>>() {}.type)
                    msgList.clear()
                    if (data != null && data.isNotEmpty()) {
                        msgList.addAll(data)
                        msgAdapter.setData(msgList)
                        msgAdapter.notifyDataSetChanged()
                    }
                } else {
                    jsonBean.let {
                        ToastUtil.show(jsonBean?.message ?: "获取消息列表失败")
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取消息列表失败")
            }
        })
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, FriendActivity::class.java))
        }
    }

}
