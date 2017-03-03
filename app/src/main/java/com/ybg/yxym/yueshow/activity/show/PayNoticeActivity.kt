package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.http.HttpUrl
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
                }
            }
        }
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
    fun start(context: Context, show: YueShow) {
        val starter = Intent(context, PayNoticeActivity::class.java)
        starter.putExtra("show", show)
        context.startActivity(starter)
    }

}
