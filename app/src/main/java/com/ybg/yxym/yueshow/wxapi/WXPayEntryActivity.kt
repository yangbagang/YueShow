package com.ybg.yxym.yueshow.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.tencent.mm.sdk.constants.ConstantsAPI
import com.tencent.mm.sdk.modelbase.BaseReq
import com.tencent.mm.sdk.modelbase.BaseResp
import com.tencent.mm.sdk.openapi.IWXAPI
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler
import com.tencent.mm.sdk.openapi.WXAPIFactory
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.pay.Constants
import com.ybg.yxym.yueshow.utils.ToastUtil

class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    private var api: IWXAPI? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_result)

        api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID)
        api!!.registerApp(Constants.WECHAT_APP_ID)
        api!!.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {
    }

    override fun onResp(resp: BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            var result = ""
            when (resp.errCode) {
                BaseResp.ErrCode.ERR_OK -> result = "微信支付成功"
                BaseResp.ErrCode.ERR_COMM -> result = "支付发生异常"
                BaseResp.ErrCode.ERR_USER_CANCEL -> result = "已取消微信支付"
            }//支付成功发送广播刷新余额
            ToastUtil.toast(result)
            finish()
        }
    }

    companion object {

        private val TAG = "WXPayEntryActivity"
    }
}