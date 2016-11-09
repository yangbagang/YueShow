package com.ybg.yxym.yueshow.app

import com.ybg.yxym.yb.app.YbgAPP
import com.ybg.yxym.yueshow.activity.user.LoginActivity
import com.ybg.yxym.yueshow.utils.ToastUtil

/**
 * Created by yangbagang on 2016/10/27.
 */

class ShowApplication : YbgAPP() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        var instance: ShowApplication? = null
            private set
    }

    fun checkNeedLogin(message: String) = message.contains("重新登录")
}
