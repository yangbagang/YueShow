package com.ybg.yxym.yueshow.activity.show

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class ShowPriceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_price)
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_show_price
    }

    override fun setUpView() {
        setCustomTitle("芝麻开门")
    }

    override fun init() {

    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.tv_show_38 -> {
                setPrice(38)
            }
            R.id.tv_show_58 -> {
                setPrice(58)
            }
            R.id.tv_show_88 -> {
                setPrice(88)
            }
        }
    }

    private fun setPrice(price: Int) {
        intent.putExtra(SHOW_PRICE, price)
        setResult(SHOW_PRICE_RESULT_CODE, intent)
        finish()
    }

    companion object {
        val SHOW_PRICE = "show_price"
        val SHOW_PRICE_REQUEST_CODE = 100
        val SHOW_PRICE_RESULT_CODE = 101
    }
}
