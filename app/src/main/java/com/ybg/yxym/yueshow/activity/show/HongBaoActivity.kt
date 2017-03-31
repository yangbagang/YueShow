package com.ybg.yxym.yueshow.activity.show

import android.os.Bundle
import android.view.View
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class HongBaoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hong_bao)
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_hong_bao
    }

    override fun setUpView() {
        setCustomTitle("礼物")
    }

    override fun init() {

    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.tv_gift_5000 -> {
                setGift(5000)
            }
            R.id.tv_gift_2333 -> {
                setGift(2333)
            }
            R.id.tv_gift_999 -> {
                setGift(999)
            }
            R.id.tv_gift_888 -> {
                setGift(888)
            }
            R.id.tv_gift_666 -> {
                setGift(666)
            }
            R.id.tv_gift_500 -> {
                setGift(500)
            }
        }
    }

    private fun setGift(gift: Int) {
        intent.putExtra(HONG_BAO, gift)
        setResult(HONG_BAO_RESULT_CODE, intent)
        finish()
    }

    companion object {
        val HONG_BAO = "hong_bao"
        val HONG_BAO_REQUEST_CODE = 200
        val HONG_BAO_RESULT_CODE = 201
    }
}
