package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.utils.UserBaseComparator
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.GroupMemberAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.ContactSideBar
import kotlinx.android.synthetic.main.activity_group_member.*
import java.util.*

class GroupMemberActivity : BaseActivity() {

    private var memberList: MutableList<UserBase> = ArrayList<UserBase>()
    private lateinit var memberAdapter: GroupMemberAdapter

    private var groupId = 0L

    override fun setContentViewId(): Int {
        return R.layout.activity_group_member
    }

    override fun setUpView() {
        setCustomTitle("添加成员")
    }

    override fun init() {
        if (intent != null) {
            groupId = intent.extras.getLong("groupId")
        }

        memberAdapter = GroupMemberAdapter(mContext!!)
        lv_contact.adapter = memberAdapter

        // 设置字母导航触摸监听
        ct_side_bar.setOnTouchingLetterChangedListener(object : ContactSideBar.OnTouchingLetterChangedListener {
            override fun onTouchingLetterChanged(s: String) {
                // 该字母首次出现的位置
                val position = memberAdapter.getPositionForSelection(s[0].toInt())
                if (position != -1) {
                    lv_contact.setSelection(position)
                }
            }
        })
        getContactList()
    }

    private fun getContactList() {
        SendRequest.getMemberList(mContext!!, mApplication.token, "$groupId", object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val data = mGson!!.fromJson<List<UserBase>>(jsonBean.data, object :
                            TypeToken<List<UserBase>>() {}.type)
                    memberList.clear()
                    if (data != null && data.isNotEmpty()) {
                        // 数据在放在adapter之前需要排序
                        Collections.sort(data, UserBaseComparator())
                        memberList.addAll(data)
                        memberAdapter.setData(data)
                        memberAdapter.notifyDataSetChanged()
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
        fun start(context: Context, groupId: Long) {
            val intent = Intent(context, GroupMemberActivity::class.java)
            intent.putExtra("groupId", groupId)
            context.startActivity(intent)
        }
    }
}
