package com.ybg.yxym.yueshow.pay

import android.app.Activity

abstract class BasePayUtils {

    protected var TAG = javaClass.simpleName
    protected var mActivity: Activity? = null

    protected var mPayType: Int = 0

    abstract fun pay(params: PayParams)

    abstract fun checkSupportPay(): Boolean

    //设置支付类型,对应后台的支付回调,钱包充值 type = 0 订单付款 type = 1
    //AliPayUtil重写了这个方法,wxPayUtils 不需要重写这个方法
    open fun setPayType(type: Int) {
        mPayType = type
    }

    fun setChatInfo(hxId: String, userId: String, userName: String, userAvatar: String) {
        mHxId = hxId
        mUserAvatar = userAvatar
        mUserName = userName
        mUserId = userId
    }

    companion object {
        val TYPE_WALLET = 0
        val TYPE_ORDER = 1

        //this is for user chat
        var mHxId: String = ""
        var mUserId: String = ""
        var mUserName: String = ""
        var mUserAvatar: String = ""
    }
}
