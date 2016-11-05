package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.view.View
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.ToastUtil

/**
 * 类描述：主播结束页面
 */
class EndingLiveActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_ending_live
    }

    override fun setUpView() {

    }

    override fun init() {

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_share_sina -> ToastUtil.show("分享到新浪微博")
            R.id.iv_share_weixin -> ToastUtil.show("分享到微信")
            R.id.iv_share_friend -> ToastUtil.show("分享到朋友圈")
            R.id.iv_share_qq -> ToastUtil.show("分享到QQ")
            R.id.iv_share_space -> ToastUtil.show("分享到空间")
            R.id.btn_back_home -> finish()
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, EndingLiveActivity::class.java)
            context.startActivity(starter)
        }
    }
}
