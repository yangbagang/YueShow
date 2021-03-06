package com.ybg.yxym.yueshow.activity.gift

import android.app.Activity
import android.content.Intent
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser

class GiftListActivity : BaseActivity() {

    var sendMsgFlag = 1

    override fun setContentViewId(): Int {
        return R.layout.activity_gift_list
    }

    override fun setUpView() {
        setCustomTitle("礼品中心")
    }

    override fun init() {
        if (intent != null) {
            val userId = intent.extras.getLong("userId")
            val ymCode = intent.extras.getString("ymCode")
            sendMsgFlag = intent.extras.getInt("sendMsgFlag", 1)
            if (userId != null && userId != 0L) {
                loadingGiftFragment(userId, ymCode)
            } else if (ymCode != null && ymCode != "") {
                SendRequest.getUserId(mContext!!, ymCode, object : OkCallback<String>(OkStringParser()){
                    override fun onSuccess(code: Int, response: String) {
                        val jsonBean = JSonResultBean.fromJSON(response)
                        if (jsonBean != null && jsonBean.isSuccess) {
                            val uid = jsonBean.data.toLong()
                            runOnUiThread {
                                loadingGiftFragment(uid, ymCode)
                            }
                        }
                    }

                    override fun onFailure(e: Throwable) {

                    }
                })
            }
        }
    }

    private fun loadingGiftFragment(userId: Long, ymCode: String) {
        val giftListFragment = GiftListFragment(userId, sendMsgFlag, ymCode)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.giftFragment, giftListFragment)
        transaction.commit()
    }

    companion object {
        fun start(context: Activity, userId: Long, ymCode: String, sendMsgFlag: Int) {
            val starter = Intent(context, GiftListActivity::class.java)
            starter.putExtra("userId", userId)
            starter.putExtra("ymCode", ymCode)
            starter.putExtra("sendMsgFlag", sendMsgFlag)
            context.startActivity(starter)
        }
    }

}
