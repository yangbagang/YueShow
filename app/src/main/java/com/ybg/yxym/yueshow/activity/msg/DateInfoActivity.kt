package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class DateInfoActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_date_info
    }

    override fun setUpView() {
        setCustomTitle("发起约会")
    }

    override fun init() {
        if (intent != null) {
            val userId = intent.extras.getLong("userId")
        }
    }

    companion object {
        fun start(context: Context, userId: Long) {
            val dateIntent = Intent(context, DateInfoActivity::class.java)
            dateIntent.putExtra("userId", userId)
            context.startActivity(dateIntent)
        }
    }
}
