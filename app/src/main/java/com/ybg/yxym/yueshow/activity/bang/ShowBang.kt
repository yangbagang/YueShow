package com.ybg.yxym.yueshow.activity.bang

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_show_bang.*
import java.util.*

/**
 * Created by yangbagang on 2016/12/2.
 */
class ShowBang : BaseActivity() {

    private var type: Int = 1
    private val titles = arrayOf("中华悦美女神", "美力榜", "活力新人王", "人气新人王", "豪气榜")

    private val mFragment = ArrayList<Fragment>()
    private var adapter: ContentViewPagerAdapter? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_show_bang
    }

    override fun setUpView() {

    }

    override fun init() {
        if (intent != null) {
            type = intent.extras.getInt("type")
        }

        mFragment.add(DayBangFragment.getInstance(type))
        mFragment.add(MonthBangFragment.getInstance(type))
        adapter = ContentViewPagerAdapter(supportFragmentManager)
        vp_main_content.adapter = adapter

        sliding_tabs.addTab(sliding_tabs.newTab().setText("日榜"))
        sliding_tabs.addTab(sliding_tabs.newTab().setText("月榜"))
        sliding_tabs.setupWithViewPager(vp_main_content)

        setCustomTitle(titles[type])
    }

    /**
     * ViewPager 适配器
     */
    internal inner class ContentViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var mTitles = arrayOf("日榜", "月榜")

        override fun getCount(): Int {
            return mTitles.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragment[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mTitles[position]
        }

    }

    companion object {

        fun start(context: Context, type: Int) {
            val starter = Intent(context, ShowBang::class.java)
            starter.putExtra("type", type)
            context.startActivity(starter)
        }
    }
}
