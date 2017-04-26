package com.ybg.yxym.yueshow.activity.gift

import android.app.Activity
import android.content.Intent
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class GiftListActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_gift_list
    }

    override fun setUpView() {
        setCustomTitle("礼品中心")
    }

    override fun init() {
        if (intent != null) {
            val userId = intent.extras.getLong("userId")
            val giftListFragment = GiftListFragment(userId)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.giftFragment, giftListFragment)
            transaction.commit()
        }
    }

    fun start(context: Activity, userId: Long) {
        val starter = Intent(context, GiftListActivity::class.java)
        starter.putExtra("userId", userId)
        context.startActivity(starter)
    }

}
