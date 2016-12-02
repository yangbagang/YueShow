package com.ybg.yxym.yueshow.activity.bang

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment

/**
 * Created by yangbagang on 2016/12/2.
 */
class MonthBangFragment : BaseFragment() {

    var type = 1

    override fun setContentViewId(): Int {
        return R.layout.fragment_bang_list
    }

    override fun setUpView() {

    }

    override fun init() {
        println("in monthBang: type=$type")
    }

    companion object {
        var bang: MonthBangFragment? = null
        fun getInstance(type: Int): MonthBangFragment {
            if (bang != null) {
                bang!!.type = type
                return bang!!
            }
            bang = MonthBangFragment()
            bang!!.type = type
            return bang!!
        }
    }

}