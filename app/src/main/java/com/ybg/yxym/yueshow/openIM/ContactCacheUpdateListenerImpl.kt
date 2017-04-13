package com.ybg.yxym.yueshow.openIM

import com.alibaba.mobileim.contact.IYWContactCacheUpdateListener
import com.alibaba.mobileim.utility.IMNotificationUtils
import com.ybg.yxym.yueshow.app.ShowApplication

class ContactCacheUpdateListenerImpl : IYWContactCacheUpdateListener {

    /**
     * 好友缓存发生变化(联系人备注修改、联系人新增和减少等)，可以刷新使用联系人缓存的UI
     * 该回调在UI线程回调 ，请勿做太重的操作
     *
     * @param currentUserid                 当前登录账户
     * @param currentAppkey                 当前Appkey
     */
    override fun onFriendCacheUpdate(currentUserid: String, currentAppkey: String) {
        IMNotificationUtils.getInstance().showToast(ShowApplication.instance, "好友缓存发生变化")
    }
}
