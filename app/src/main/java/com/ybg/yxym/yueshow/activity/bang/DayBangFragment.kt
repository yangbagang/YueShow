package com.ybg.yxym.yueshow.activity.bang

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment

/**
 * Created by yangbagang on 2016/12/2.
 */
class DayBangFragment : BaseFragment() {

    var type = 1

    override fun setContentViewId(): Int {
        return R.layout.fragment_bang_list
    }

    override fun setUpView() {

    }

    override fun init() {
        println("in dayBang: type=$type")
    }

    companion object {
        var bang: DayBangFragment? = null
        fun getInstance(type: Int): DayBangFragment {
            if (bang != null) {
                bang!!.type = type
                return bang!!
            }
            bang = DayBangFragment()
            bang!!.type = type
            return bang!!
        }
    }

}