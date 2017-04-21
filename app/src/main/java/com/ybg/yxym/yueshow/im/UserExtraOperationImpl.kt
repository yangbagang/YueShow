package com.ybg.yxym.yueshow.im

import com.ybg.yxym.im.extra.UserInfoExtra
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.activity.msg.FriendInfoActivity
import com.ybg.yxym.yueshow.activity.msg.UserInfoActivity
import com.ybg.yxym.yueshow.activity.user.PersonCenterActivity
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser

/**
 * Created by yangbagang on 2017/4/20.
 */
class UserExtraOperationImpl : UserInfoExtra.UserExtraOperation {

    override fun onAvatarClick(userId: String?) {
        if (userId == null) {
            return
        }
        val application = ShowApplication.instance!!
        if (userId == application.ymCode) {
            PersonCenterActivity.start(application)
            return
        }
        SendRequest.checkFriend(application, application.token, userId, object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.errorCode == "0") {
                    if (jsonBean.isSuccess) {
                        FriendInfoActivity.start(application, userId)
                    } else {
                        UserInfoActivity.start(application, userId)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                e.printStackTrace()
            }
        })
    }

    override fun onLoginCallback(status: Int, desc: String?) {
        println("status=$status, desc=$desc")
    }

}