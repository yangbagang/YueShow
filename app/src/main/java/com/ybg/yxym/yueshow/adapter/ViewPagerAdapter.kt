package com.ybg.yxym.yueshow.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var mList: List<Fragment>? = null

    fun setDataList(list: List<Fragment>) {
        this.mList = list
    }

    override fun getItem(position: Int): Fragment? {
        return if (mList != null && mList!!.size > position) mList!![position] else null
    }

    override fun getCount(): Int {
        return if (mList != null && mList!!.size > 0) mList!!.size else 0
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
        //禁用了destroyItem方法,所以只适合Fragment页面固定的情况下使用
    }
}
