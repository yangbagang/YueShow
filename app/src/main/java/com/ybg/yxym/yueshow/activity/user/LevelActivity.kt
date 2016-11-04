package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.user.fragment.BeautyLevelFragment
import com.ybg.yxym.yueshow.activity.user.fragment.BeautyLevelPowerFragment
import com.ybg.yxym.yueshow.activity.user.fragment.BeautySecretFragment

import java.util.ArrayList

/**
 * 类描述：徽章与等级页面
 */
class LevelActivity : BaseActivity() {

    private var slidingTabs: TabLayout? = null
    private var vpMainContent: ViewPager? = null

    private val mFragment = ArrayList<Fragment>()
    private var adatper: ViewPagerFragmentAdatper? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_level
    }

    override fun setUpView() {
        slidingTabs = findViewById(R.id.sliding_tabs) as TabLayout
        vpMainContent = findViewById(R.id.vp_main_content) as ViewPager
        mContext = this@LevelActivity
        setCustomTitle("徽章与美力")
    }

    override fun init() {
        mFragment.add(BeautySecretFragment.instance!!)
        mFragment.add(BeautyLevelFragment.instance!!)
        mFragment.add(BeautyLevelPowerFragment.instance!!)
        adatper = ViewPagerFragmentAdatper(supportFragmentManager)
        vpMainContent!!.adapter = adatper
        slidingTabs!!.addTab(slidingTabs!!.newTab().setText("美力秘籍"))
        slidingTabs!!.addTab(slidingTabs!!.newTab().setText("美力等级"))
        slidingTabs!!.addTab(slidingTabs!!.newTab().setText("等级特权"))
        slidingTabs!!.setupWithViewPager(vpMainContent)
    }

    /**
     * ViewPager 适配器
     */
    internal inner class ViewPagerFragmentAdatper(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var titles = arrayOf("美力秘籍", "美力等级", "等级特权")

        override fun getCount(): Int {
            return mFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragment[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }

    companion object {

        /**
         * @param context 跳转到本页面
         */
        fun start(context: Context) {
            val starter = Intent(context, LevelActivity::class.java)
            context.startActivity(starter)
        }
    }
}

