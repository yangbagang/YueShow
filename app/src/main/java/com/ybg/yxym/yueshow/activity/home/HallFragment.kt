package com.ybg.yxym.yueshow.activity.home

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment

import java.util.ArrayList

/**
 * 悦秀厅
 */
class HallFragment : BaseFragment() {

    private lateinit var slidingTabs: TabLayout
    private lateinit var vpMainContent: ViewPager

    private val mFragment = ArrayList<Fragment>()
    private var adapter: ContentViewPagerAdapter? = null

    override fun setContentViewId(): Int {
        return R.layout.fragment_hall
    }

    override fun setUpView() {
        slidingTabs = mRootView!!.findViewById(R.id.sliding_tabs) as TabLayout
        vpMainContent = mRootView!!.findViewById(R.id.vp_main_content) as ViewPager
    }

    override fun init() {
        mFragment.add(HotFragment.instance!!)
        mFragment.add(FriendShowFragment.instance!!)
        mFragment.add(FreshFragment.instance!!)
        adapter = ContentViewPagerAdapter(childFragmentManager)
        vpMainContent.adapter = adapter

        slidingTabs.addTab(slidingTabs.newTab().setText("最热"))
        slidingTabs.addTab(slidingTabs.newTab().setText("友秀圈"))
        slidingTabs.addTab(slidingTabs.newTab().setText("最鲜"))
        slidingTabs.setupWithViewPager(vpMainContent)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * ViewPager 适配器
     */
    internal inner class ContentViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var mTitles = arrayOf("最热", "友秀圈", "新鲜")

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

        fun newInstance(): HallFragment {
            val args = Bundle()
            val fragment = HallFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
