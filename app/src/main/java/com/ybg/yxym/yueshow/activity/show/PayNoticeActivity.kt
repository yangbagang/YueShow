package com.ybg.yxym.yueshow.activity.show

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.gift.CardListActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_pay_notice.*

class PayNoticeActivity : BaseActivity() {

    private lateinit var show: YueShow

    override fun setContentViewId(): Int {
        return R.layout.activity_pay_notice
    }

    override fun setUpView() {
        setCustomTitle("付费阅读")
    }

    override fun init() {
        if (intent != null) {
            val showItem = intent.getSerializableExtra("show")
            if (showItem is YueShow) {
                show = showItem
                tv_price_notice.text = String.format("%d 美票", show.price)
                setImageProcess(show.thumbnail)

                tv_pay_notice.setOnClickListener {
                    //支付流程
                    payForShow()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentExtra.RequestCode.REQUEST_CHONG_ZHI && resultCode == Activity.RESULT_OK) {
            payForShow()
        }
    }

    private fun payForShow() {
        SendRequest.payForShow(mContext!!, mApplication.token, show.id!!, object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    if (jsonBean.data == "true") {
                        val responseIntent = Intent(mContext, ShowDetailActivity::class.java)
                        responseIntent.putExtra("payResult", true)
                        setResult(Activity.RESULT_OK, responseIntent)
                        finish()
                    } else if (jsonBean.data == "false") {
                        CardListActivity.start(mContext!!)
                    }
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    private fun setImageProcess(fid: String) {
        val imageUrl = HttpUrl.getImageUrl(fid)
        ImageLoader.getInstance().loadImage(imageUrl, object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                super.onLoadingComplete(imageUri, view, loadedImage)
                val bitmap = BitmapUtils.fastBlur(loadedImage, 8)
                bitmap?.let {
                    iv_pay_notice.setImageBitmap(bitmap)
                }
            }
        })
    }

    companion object {
        fun start(context: Activity, show: YueShow) {
            val starter = Intent(context, PayNoticeActivity::class.java)
            starter.putExtra("show", show)
            context.startActivityForResult(starter, IntentExtra.RequestCode.REQUEST_PAY_NOTICE)
        }
    }

}
