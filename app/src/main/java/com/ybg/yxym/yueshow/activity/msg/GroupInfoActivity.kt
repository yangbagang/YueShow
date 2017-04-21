package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class GroupInfoActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_group_info
    }

    override fun setUpView() {
        setCustomTitle("群信息")
    }

    override fun init() {

    }

    companion object {
        fun start(context: Context, groupId: Long) {
            val startIntent = Intent(context, GroupInfoActivity::class.java)
            startIntent.putExtra("groupId", groupId)
            context.startActivity(startIntent)
        }
    }
}
