package com.ybg.yxym.yueshow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igexin.sdk.PushConsts
import com.ybg.yxym.yb.bean.LiveMsg
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yueshow.activity.gift.CardPaymentActivity
import com.ybg.yxym.yueshow.activity.live.LivingActivity
import com.ybg.yxym.yueshow.activity.live.ShowLiveActivity
import org.json.JSONObject

/**
 * Created by yangbagang on 2017/1/26.
 */
class PushMsgReceiver : BroadcastReceiver() {

    private val USER_ENTER_LIVE = "user_enter_live"
    private val USER_LEAVE_LIVE = "user_leave_live"
    private val LIVE_MSG = "live_msg"
    private val PAY_CALL_BACK = "pay_call_back"

    private val gson = Gson()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        val bundle = intent.extras
        val cmd = bundle.getInt(PushConsts.CMD_ACTION)
        if (cmd == PushConsts.GET_MSG_DATA) {
            val payload = bundle.getByteArray("payload")
            if (payload != null) {
                parseMsg(String(payload))
            }
        }
    }

    private fun parseMsg(msg: String) {
        try {
            val json = JSONObject(msg)
            val type = json.getString("type")
            val data = json.getString("data")
            when(type) {
                USER_ENTER_LIVE -> {
                    val userBase = gson.fromJson<UserBase>(data, object : TypeToken<UserBase>(){}.type)
                    LivingActivity.instance?.userEnter(userBase)
                    ShowLiveActivity.instance?.userEnter(userBase)
                }
                USER_LEAVE_LIVE -> {
                    val userBase = gson.fromJson<UserBase>(data, object : TypeToken<UserBase>(){}.type)
                    LivingActivity.instance?.userLeave(userBase)
                    ShowLiveActivity.instance?.userLeave(userBase)
                }
                LIVE_MSG -> {
                    val liveMsg = gson.fromJson<LiveMsg>(data, object : TypeToken<LiveMsg>(){}.type)
                    LivingActivity.instance?.addMsg(liveMsg)
                    ShowLiveActivity.instance?.addMsg(liveMsg)
                }
                PAY_CALL_BACK -> {
                    CardPaymentActivity.instance?.payFinish()
                }
                else -> {
                    println("type=$type, data=$data")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}