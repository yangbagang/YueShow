package com.ybg.yxym.yueshow.pay

import android.app.Activity

import com.ybg.yxym.yueshow.pay.alipay.AliPayUtil
import com.ybg.yxym.yueshow.pay.weixinpay.WxPayUtils


/**
 * @author Jax
 * *
 * @version V1.0.0
 * *
 * @Created on 2015/10/26 9:56.
 */
object PayUtilsFactory {
    val PAY_TYPE_ALIPAY = 2//支付宝支付
    val PAY_TYPE_WECHAT = 3//微信支付
    val PAY_TYPE_YIPAY = 4//翼支付
    private var mPayUtils: BasePayUtils? = null

    fun getPayUtils(payType: Int, act: Activity): BasePayUtils? {
        when (payType) {
            PAY_TYPE_ALIPAY -> mPayUtils = AliPayUtil(act)
            PAY_TYPE_WECHAT -> mPayUtils = WxPayUtils(act)
            PAY_TYPE_YIPAY -> {
            }
        }
        return mPayUtils

    }
}
