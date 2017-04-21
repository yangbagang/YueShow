package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.im.extra.UserInfoExtra
import com.ybg.yxym.yb.bean.Friend
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.bean.UserInfo
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_friend_info.*
import org.json.JSONObject

class FriendInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_info)
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_friend_info
    }

    override fun setUpView() {
        setCustomTitle("好友资料")
    }

    override fun init() {
        if (intent != null) {
            val ymCode = intent.extras.getString("ymCode")
            getFriendDetailInfo(ymCode)
        }
    }

    private fun getFriendDetailInfo(ymCode: String) {
        SendRequest.getFriendInfo(mContext!!, mApplication.token, ymCode, object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    try {
                        val base = JSONObject(jsonBean.data)
                        val userBase = mGson!!.fromJson<UserBase>(base.getString("userBase"),
                                object : TypeToken<UserBase>(){}.type)
                        val friend = mGson!!.fromJson<Friend>(base.getString("friend"),
                                object : TypeToken<Friend>(){}.type)
                        val userInfo = mGson!!.fromJson<UserInfo>(base.getString("userInfo"),
                                object : TypeToken<UserInfo>(){}.type)
                        runOnUiThread {
                            ImageLoaderUtils.instance.loadBitmap(friend_detail_avatar, HttpUrl
                                    .getImageUrl(userBase.avatar))
                            nick_name_tv.text = userBase.nickName
                            note_name_tv.text = friend.nickName
                            tv_user_gender.text = if (userInfo.sex == 1) "男" else "女"
                            region_tv.text = "${userInfo.province}${userInfo.city}"
                            signature_tv.text = userBase.ymMemo
                            swt_disturb_slip.isChecked = friend.disturbing != 1
                            swt_black_list.isChecked = friend.inBlacklist == 1
                        }
                        btn_send_msg.setOnClickListener {
                            UserInfoExtra.getInstance().sendMsg(userBase.ymCode)
                        }
                        btn_delete_friend.setOnClickListener {
                            deleteFriend(friend)
                        }
                        swt_disturb_slip.setOnCheckedChangeListener { buttonView, isChecked ->
                            changeDisturbState(friend.id, isChecked)
                        }
                        swt_black_list.setOnCheckedChangeListener { buttonView, isChecked ->
                            changeBlackListState(friend.id, isChecked)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    private fun deleteFriend(friend: Friend) {
        SendRequest.deleteFriend(mContext!!, mApplication.token, friend.id, object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    ToastUtil.show("操作成功")
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("操作失败")
            }
        })
    }

    private fun changeDisturbState(friendId: Long, isChecked: Boolean) {
        val disturbing = if (isChecked) 0 else 1
        SendRequest.changeDisturbState(mContext!!, mApplication.token, friendId, disturbing,
                object : OkCallback<String>(OkStringParser()){
                    override fun onSuccess(code: Int, response: String) {
                        val jsonBean = JSonResultBean.fromJSON(response)
                        if (jsonBean != null && jsonBean.isSuccess) {
                            ToastUtil.show("操作成功")
                        } else {
                            jsonBean?.let {
                                ToastUtil.show(jsonBean.message)
                            }
                        }
                    }

                    override fun onFailure(e: Throwable) {
                        ToastUtil.show("操作失败")
                    }
                })
    }

    private fun changeBlackListState(friendId: Long, isChecked: Boolean) {
        val inBlacklist = if (isChecked) 1 else 0
        SendRequest.changeBlackListState(mContext!!, mApplication.token, friendId, inBlacklist,
                object : OkCallback<String>(OkStringParser()){
                    override fun onSuccess(code: Int, response: String) {
                        val jsonBean = JSonResultBean.fromJSON(response)
                        if (jsonBean != null && jsonBean.isSuccess) {
                            ToastUtil.show("操作成功")
                        } else {
                            jsonBean?.let {
                                ToastUtil.show(jsonBean.message)
                            }
                        }
                    }

                    override fun onFailure(e: Throwable) {
                        ToastUtil.show("操作失败")
                    }
                })
    }

    companion object {
        fun start(context: Context, ymCode: String) {
            val startIntent = Intent(context, FriendInfoActivity::class.java)
            startIntent.putExtra("ymCode", ymCode)
            context.startActivity(startIntent)
        }
    }
}
