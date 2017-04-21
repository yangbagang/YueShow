package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import com.google.gson.reflect.TypeToken
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
import kotlinx.android.synthetic.main.activity_user_info.*
import org.json.JSONObject

class UserInfoActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_user_info
    }

    override fun setUpView() {
        setCustomTitle("用户资料")
    }

    override fun init() {
        if (intent != null) {
            val ymCode = intent.extras.getString("ymCode")
            getUserDetailInfo(ymCode)
        }
    }

    private fun getUserDetailInfo(ymCode: String) {
        SendRequest.getUserDetailInfo(mContext!!, ymCode, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    try {
                        val base = JSONObject(jsonBean.data)
                        val userBase = mGson!!.fromJson<UserBase>(base.getString("userBase"),
                                object : TypeToken<UserBase>(){}.type)
                        val userInfo = mGson!!.fromJson<UserInfo>(base.getString("userInfo"),
                                object : TypeToken<UserInfo>(){}.type)
                        runOnUiThread {
                            ImageLoaderUtils.instance.loadBitmap(friend_detail_avatar, HttpUrl
                                    .getImageUrl(userBase.avatar))
                            tv_nick_name.text = userBase.nickName
                            tv_user_gender.text = if (userInfo.sex == 1) "男" else "女"
                            region_tv.text = "${userInfo.province}${userInfo.city}"
                            signature_tv.text = userBase.ymMemo
                        }
                        add_to_friend.setOnClickListener {
                            addFriend(userBase)
                        }
                        bt_gift.setOnClickListener {
                            gift(userBase)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("加载信息失败")
            }
        })
    }

    private fun addFriend(userBase: UserBase) {
        SendRequest.createFriendRequest(mContext!!, mApplication.token, userBase.id, "", object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    ToastUtil.show("请求己发送")
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("添加好友失败")
            }
        })
    }

    private fun gift(userBase: UserBase) {
        //TODO 送礼
    }

    companion object {
        fun start(context: Context, ymCode: String) {
            val startIntent = Intent(context, UserInfoActivity::class.java)
            startIntent.putExtra("ymCode", ymCode)
            context.startActivity(startIntent)
        }
    }
}
