package com.ybg.yxym.yueshow.wxapi

import android.os.Bundle

import com.google.gson.Gson
import com.tencent.mm.sdk.modelbase.BaseReq
import com.tencent.mm.sdk.modelbase.BaseResp
import com.umeng.socialize.weixin.view.WXCallbackActivity
import com.ybg.yxym.yb.utils.LogUtil

class WXEntryActivity : WXCallbackActivity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResp(resp: BaseResp?) {
        super.onResp(resp)
        LogUtil.d(TAG + ":" + Gson().toJson(resp))
    }

    override fun onReq(req: BaseReq) {
        super.onReq(req)
        LogUtil.d(TAG + ":" + Gson().toJson(req))
    }
}
