package com.ybg.yxym.yueshow.activity.video

import android.content.Context
import android.content.Intent
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class VideoShotActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_video_shot
    }

    override fun setUpView() {

    }

    override fun init() {

    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, VideoShotActivity::class.java)
            context.startActivity(starter)
        }
    }
}
