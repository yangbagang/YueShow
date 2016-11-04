package com.ybg.yxym.yueshow.pay.weixinpay

import android.app.Activity
import android.os.Handler
import android.text.TextUtils

import com.tencent.mm.sdk.constants.Build
import com.tencent.mm.sdk.modelpay.PayReq
import com.tencent.mm.sdk.openapi.IWXAPI
import com.tencent.mm.sdk.openapi.WXAPIFactory
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.http.OkHttpProxy
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.pay.BasePayUtils
import com.ybg.yxym.yueshow.pay.Constants
import com.ybg.yxym.yueshow.pay.PayParams
import com.ybg.yxym.yueshow.utils.ToastUtil

import org.json.JSONException
import org.json.JSONObject


/**
 * 微信支付工具类
 * 微信支付前先配置微信登录部分,确保能进行微信登录(部分情况下有可能是微信缓存导致不能调用微信客户端)
 * 支付只需正确配置 mAppId, mPartnerId,(这2个参数客户端配置) mPrepayId, mNonceStr, mTimeStamp, mPackageValue,mSign;(这几个参数后台返回)
 * 如果提示==>"支付发生异常" 而errCode = -1 可能是微信缓存问题,这个时候清理下微信的缓存重试下
 */
class WxPayUtils
/**
 * 充值构造
 */
(activity: Activity) : BasePayUtils() {
    private val api: IWXAPI
    private val mHandler: Handler
    private var mAppId: String? = null
    private var mPartnerId: String? = null
    private var mPrepayId: String? = null
    private var mNonceStr: String? = null
    private var mTimeStamp: String? = null
    private var mPackageValue: String? = null
    private var mSign: String? = null

    init {
        this.mActivity = activity
        this.mHandler = Handler()
        api = WXAPIFactory.createWXAPI(mActivity, Constants.WECHAT_APP_ID)
    }

    override fun checkSupportPay(): Boolean {
        val isPaySupported = api.wxAppSupportAPI >= Build.PAY_SUPPORTED_SDK_INT
        if (!isPaySupported) {
            ToastUtil.toast("请安装最新版本微信!")
        }
        return isPaySupported
    }

    //开发阶段用微信提供的测试数据,后台有数据时将
    override fun pay(params: PayParams) {
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        if (Constants.PAY_DEBUG_MODE) {
            val url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android"
            OkHttpProxy.get(url, mActivity!!, emptyMap(), object : OkCallback<String>(OkStringParser()) {
                override fun onSuccess(code: Int, response: String) {
                    LogUtil.d(TAG + response)
                    try {
                        val obj = JSONObject(response)
                        mAppId = obj.optString("appid")
                        mPartnerId = obj.optString("partnerid")
                        mPrepayId = obj.optString("prepayid")
                        mNonceStr = obj.optString("noncestr")
                        mTimeStamp = obj.optString("timestamp")
                        mPackageValue = obj.optString("package")
                        mSign = obj.optString("sign")
                        sendPayReq()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(e: Throwable) {
                    LogUtil.e(TAG + e.message)
                }
            })
        } else {
            if (params == null || TextUtils.isEmpty(params.prepayid) || TextUtils.isEmpty(params.noncestr) || TextUtils.isEmpty(params.timestamp) || TextUtils.isEmpty(params.`package`) || TextUtils.isEmpty(params.sign)) {
                ToastUtil.toast("支付信息不全，请重新选择！")
                return
            }
            mAppId = Constants.WECHAT_APP_ID
            mPartnerId = Constants.WECHAT_PARTNER_ID
            mPrepayId = params.prepayid
            mNonceStr = params.noncestr
            mTimeStamp = params.timestamp
            mPackageValue = params.`package`
            mSign = params.sign
            sendPayReq()
        }
    }

    private fun sendPayReq() {
        mHandler.post {
            val req = PayReq()
            req.appId = mAppId
            req.partnerId = mPartnerId
            req.prepayId = mPrepayId
            req.nonceStr = mNonceStr
            req.timeStamp = mTimeStamp
            req.packageValue = mPackageValue
            req.sign = mSign
            api.registerApp(Constants.WECHAT_APP_ID)
            api.sendReq(req)
        }
    }
}
