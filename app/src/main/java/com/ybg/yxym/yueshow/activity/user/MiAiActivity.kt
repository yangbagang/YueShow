package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.view.View

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

/**
 * 类描述：密爱页面
 */
class MiAiActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_mi_ai
    }

    override fun setUpView() {
        setCustomTitle("爱蜜情深")
    }

    override fun init() {
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, MiAiActivity::class.java)
            context.startActivity(starter)
        }
    }

}
