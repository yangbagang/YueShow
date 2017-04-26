package com.ybg.yxym.yueshow.activity.gift

import android.app.Activity
import android.content.Intent
import com.pingplusplus.android.Pingpp
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_card_payment.*


class CardPaymentActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_card_payment
    }

    override fun setUpView() {
        setCustomTitle("选择支付方式")//1:支付宝 2:微信支付
    }

    override fun init() {
        instance = this@CardPaymentActivity
        if (intent != null) {
            val money = intent.extras.getFloat("money")
            val orderNo = intent.extras.getString("orderNo")
            tv_money.text = "共需支付${money}元"
            tv_wx_pay.setOnClickListener {
                createCharge(orderNo, "2")
            }
            tv_zfb_pay.setOnClickListener {
                createCharge(orderNo, "1")
            }
        }
    }

    private fun createCharge(orderNo: String, payWay: String) {
        SendRequest.createCharge(mContext!!, orderNo, payWay, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    Pingpp.createPayment(this@CardPaymentActivity, jsonBean.data)
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                e.printStackTrace()
                ToastUtil.show("操作失败")
            }
        })
    }

    fun payFinish() {
        val response = Intent(mContext, CardListActivity::class.java)
        response.putExtra("payResult", true)
        setResult(Activity.RESULT_OK, response)
        finish()
    }

    companion object {

        var instance: CardPaymentActivity? = null

        fun start(context: Activity, orderNo: String, money: Float) {
            val starter = Intent(context, CardPaymentActivity::class.java)
            starter.putExtra("orderNo", orderNo)
            starter.putExtra("money", money)
            context.startActivityForResult(starter, IntentExtra.RequestCode.REQUEST_ZHI_FU)
        }
    }
}
