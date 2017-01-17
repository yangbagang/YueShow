package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity

class LivingActivity : BaseActivity() {

    private var show: YueShow? = null
    private var url: String? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_living
    }

    override fun setUpView() {
        setCustomTitle("正在直播")
    }

    override fun init() {
        if (intent != null) {
            show = intent.extras.getSerializable("show") as YueShow
            url = intent.extras.getString("url")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.complete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_finish) {
            closeLive()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun closeLive() {

    }

    companion object {
        fun start(context: Context, show: YueShow, url: String) {
            val starter = Intent(context, LivingActivity::class.java)
            starter.putExtra("show", show)
            starter.putExtra("url", url)
            context.startActivity(starter)
        }
    }

}
