package com.ybg.yxym.yueshow.activity.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView

import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.pay.BasePayUtils
import com.ybg.yxym.yueshow.pay.PayUtilsFactory
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil

/**
 * 悦美榜
 */
class ChartsFragment : BaseFragment() {

    private var m_ivImage: ImageView? = null
    private var mImage: UMImage? = null
    private var mPayUtils: BasePayUtils? = null

    override fun setContentViewId(): Int {
        return R.layout.fragment_charts
    }

    override fun setUpView() {
        m_ivImage = mRootView?.findViewById(R.id.iv_image_test) as ImageView
    }

    override fun init() {
        ImageLoaderUtils.instance.loadBitmap(m_ivImage!!, IMAGE_URL)
        mImage = UMImage(mContext, IMAGE_URL)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.tv_video_test -> {
            }
            R.id.tv_login_test -> {
            }
            R.id.tv_wechat_share//微信分享测试
            -> ShareAction(mContext).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE).withText("来自友盟分享面板").setCallback(umShareListener).withTitle("this is title").withMedia(mImage).withTargetUrl("http://app.sobig.cc").open()
            R.id.tv_weibo_share//微博分享测试
            -> {
            }
            R.id.tv_qq_share//QQ分享测试
            -> ShareAction(mContext).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener).withTitle("this is title").withText("hello umeng").withMedia(mImage).withTargetUrl("http://app.sobig.cc").withTitle("qqshare").share()
            R.id.tv_wechat_pay -> {
                mPayUtils = PayUtilsFactory.getPayUtils(PayUtilsFactory.PAY_TYPE_WECHAT, mContext!!)
                //mPayUtils!!.pay(null)
            }
            R.id.tv_ali_pay -> {
                mPayUtils = PayUtilsFactory.getPayUtils(PayUtilsFactory.PAY_TYPE_ALIPAY, mContext!!)
                //mPayUtils!!.pay(null)
            }
        }//LoginActivity.start(mContext);
    }

    private val umShareListener = object : UMShareListener {
        override fun onResult(platform: SHARE_MEDIA) {
            if (platform.name == "WEIXIN_FAVORITE") {
                ToastUtil.show("${platform}\t收藏成功啦")
            } else {
                ToastUtil.show("${platform}\t分享成功啦")
            }
        }

        override fun onError(platform: SHARE_MEDIA, t: Throwable) {
            ToastUtil.show("${platform}\t分享失败啦")
        }

        override fun onCancel(platform: SHARE_MEDIA) {
            ToastUtil.show("${platform}\t分享取消了")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(mContext).onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        val IMAGE_URL = "http://img4.duitang.com/uploads/item/201205/31/20120531170732_sSwAu.jpeg"

        fun newInstance(): ChartsFragment {

            val args = Bundle()

            val fragment = ChartsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
