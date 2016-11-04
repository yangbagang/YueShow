package com.ybg.yxym.yueshow.pay.weixinpay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.tencent.mm.sdk.openapi.IWXAPI
import com.tencent.mm.sdk.openapi.WXAPIFactory
import com.ybg.yxym.yueshow.pay.Constants

class AppRegister : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val msgApi = WXAPIFactory.createWXAPI(context, null)

        // 将该app注册到微信
        msgApi.registerApp(Constants.WECHAT_APP_ID)
    }
}
