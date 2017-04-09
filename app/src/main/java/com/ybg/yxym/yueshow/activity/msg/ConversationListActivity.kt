package com.ybg.yxym.yueshow.activity.msg

import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class ConversationListActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_conversation_list
    }

    override fun setUpView() {
        setCustomTitle("朋友")
    }

    override fun init() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.friend_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_friend) {
            //跳转到消息页面
            ContactActivity.start(this)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
