package com.ybg.yxym.yueshow.activity.gift

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.RuiGift
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil

class SendGiftActivity : BaseActivity() {

    private var userId = 0L
    private var giftId = 0L

    override fun setContentViewId(): Int {
        return R.layout.activity_send_gift
    }

    override fun setUpView() {

    }

    override fun init() {
        if (intent != null) {
            userId = intent.extras.getLong("userId")
            giftId = intent.extras.getLong("giftId")
            sendGift()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_gift)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentExtra.RequestCode.REQUEST_CHONG_ZHI && resultCode == Activity
                .RESULT_OK) {
            //充值完成，再次送礼
            sendGift()
        }
    }

    private fun sendGift() {
        SendRequest.sendGift(mContext!!, mApplication.token, userId, giftId, object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //TODO 赠送成功，发消息提醒
                    finish()
                } else {
                    if (jsonBean != null && jsonBean.errorCode == "4") {
                        //余额不足，转向充值
                        CardListActivity.start(mContext!!)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("操作失败")
            }
        })
    }

    companion object {

        fun start(context: Activity, userId: Long, giftId: Long) {
            val starter = Intent(context, SendGiftActivity::class.java)
            starter.putExtra("giftId", giftId)
            starter.putExtra("userId", userId)
            context.startActivity(starter)
        }
    }

}
