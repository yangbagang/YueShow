package com.ybg.yxym.yueshow.activity.show

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class YaoYueActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_yao_yue
    }

    override fun setUpView() {
        setCustomTitle("发布邀约")
    }

    override fun init() {

    }
}
