package com.ybg.yxym.yueshow.activity.show

import android.os.Bundle
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class MakeDateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_date)
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_make_date
    }

    override fun setUpView() {
        setCustomTitle("约TA")
    }

    override fun init() {

    }
}
