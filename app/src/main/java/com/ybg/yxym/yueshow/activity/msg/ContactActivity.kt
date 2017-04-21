package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.view.View
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.im.extra.UserInfoExtra
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.utils.UserBaseComparator
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.ContactAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.ContactSideBar
import kotlinx.android.synthetic.main.activity_contact.*
import java.util.*

class ContactActivity : BaseActivity() {

    private var userList: MutableList<UserBase> = ArrayList<UserBase>()
    private lateinit var userAdapter: ContactAdapter

    override fun setContentViewId(): Int {
        return R.layout.activity_contact
    }

    override fun setUpView() {
        setCustomTitle("通讯录")
    }

    override fun init() {
        userAdapter = ContactAdapter(mContext!!)
        lv_contact.adapter = userAdapter

        // 设置字母导航触摸监听
        ct_side_bar.setOnTouchingLetterChangedListener(object : ContactSideBar.OnTouchingLetterChangedListener {
            override fun onTouchingLetterChanged(s: String) {
                // 该字母首次出现的位置
                val position = userAdapter.getPositionForSelection(s[0].toInt())
                if (position != -1) {
                    lv_contact.setSelection(position)
                }
            }
        })
        getContactList()
        lv_contact.setOnItemClickListener { parent, view, position, id ->
            UserInfoExtra.getInstance().sendMsg(userList[position].ymCode)
        }
    }

    private fun getContactList() {
        SendRequest.getFriendList(mContext!!, mApplication.token, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val data = mGson!!.fromJson<List<UserBase>>(jsonBean.data, object :
                            TypeToken<List<UserBase>>() {}.type)
                    userList.clear()
                    if (data != null && data.isNotEmpty()) {
                        // 数据在放在adapter之前需要排序
                        Collections.sort(data, UserBaseComparator())
                        userList.addAll(data)
                        userAdapter.setData(data)
                        userAdapter.notifyDataSetChanged()
                    }
                } else {
                    jsonBean.let {
                        ToastUtil.show(jsonBean?.message ?: "获取好友列表失败")
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取好友列表失败")
            }
        })

    }

    fun goNewFriend(v: View) {
        NewFriendActivity.start(mContext!!)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ContactActivity::class.java))
        }
    }
}
