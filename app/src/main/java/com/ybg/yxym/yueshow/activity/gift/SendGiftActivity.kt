package com.ybg.yxym.yueshow.activity.gift

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ybg.yxym.im.extra.UserInfoExtra
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.RuiGift
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.constant.MessageEvent
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import org.greenrobot.eventbus.EventBus

class SendGiftActivity : BaseActivity() {

    private var userId = 0L
    private var giftId = 0L

    private var sendMsgFlag = 0
    private var ymCode = ""
    private var giftName = ""
    private var giftImgId = ""

    override fun setContentViewId(): Int {
        return R.layout.activity_send_gift
    }

    override fun setUpView() {
        setCustomTitle("正在发送礼物")
    }

    override fun init() {
        if (intent != null) {
            userId = intent.extras.getLong("userId")
            giftId = intent.extras.getLong("giftId")
            sendMsgFlag = intent.extras.getInt("sendMsgFlag")
            ymCode = intent.extras.getString("ymCode")
            giftName = intent.extras.getString("giftName")
            giftImgId = intent.extras.getString("giftImgId")
            sendGift()
        } else {
            println("intent is null")
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
        println("send gift ...")
        SendRequest.sendGift(mContext!!, mApplication.token, userId, giftId, object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //赠送成功，发消息提醒
                    if (sendMsgFlag == 0) {
                        //直播内发送事件
                        val message = MessageEvent(MessageEvent.MESSAGE_SEND_GIFT, "$giftName,$giftImgId")
                        EventBus.getDefault().post(message)
                    } else {
                        //非直播内调用消息模块发送消息
                        UserInfoExtra.getInstance().sendGiftMsg(ymCode, giftName, giftImgId)
                        ToastUtil.show("礼品己发送")
                    }
                    finish()
                } else {
                    if (jsonBean != null && jsonBean.errorCode == "4") {
                        //余额不足，转向充值
                        CardListActivity.start(mContext!!)
                    } else {
                        jsonBean?.let {
                            ToastUtil.show(jsonBean.data)
                        }
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("操作失败")
            }
        })
    }

    companion object {

        fun start(context: Activity, userId: Long, giftId: Long, sendMsgFlag: Int, ymCode: String, giftName: String, giftImgId: String) {
            val starter = Intent(context, SendGiftActivity::class.java)
            starter.putExtra("giftId", giftId)
            starter.putExtra("userId", userId)
            starter.putExtra("sendMsgFlag", sendMsgFlag)
            starter.putExtra("ymCode", ymCode)
            starter.putExtra("giftName", giftName)
            starter.putExtra("giftImgId", giftImgId)
            context.startActivity(starter)
        }
    }

}
