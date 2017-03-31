package com.ybg.yxym.yueshow.activity.show

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_show_post.*

/**
 * Created by yangbagang on 2016/11/17.
 */
abstract class PostShowActivity : BaseActivity() {

    protected var title = ""
    protected var price = 0

    fun onClick(view: View) {
        view.isEnabled = false
        //检查数据完整性
        checkAndPost()
    }

    fun checkAndPost() {
        //检查数据完整性
        title = et_show_title.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtil.show("描述不能为空。")
            return
        }
        postShow()
    }

    abstract fun postShow()

    fun selectShowPrice(view: View) {
        val intent = Intent(this, ShowPriceActivity::class.java)
        startActivityForResult(intent, ShowPriceActivity.SHOW_PRICE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ShowPriceActivity.SHOW_PRICE_REQUEST_CODE
                && resultCode == ShowPriceActivity.SHOW_PRICE_REQUEST_CODE) {
            if (data != null) {
                price = data.getIntExtra(ShowPriceActivity.SHOW_PRICE, 0)
                tv_show_price.text = String.format("%d美票", price)
            }
        }
    }
}