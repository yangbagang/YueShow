package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.im.activity.ConversationListFragment
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class FriendActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_friend
    }

    override fun setUpView() {
        setCustomTitle("朋友")
    }

    override fun init() {
        //val transaction = supportFragmentManager.beginTransaction()
        //val msgFragment = ConversationListFragment()
        //transaction.replace(R.id.msgFragment, msgFragment)
        //transaction.commit()
        //msgFragment.sortConvList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.friend_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_friend) {
            //跳转到消息页面
            ContactActivity.start(this@FriendActivity)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, FriendActivity::class.java))
        }
    }

}
