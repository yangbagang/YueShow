package com.ybg.yxym.yueshow.activity.user.fragment

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment

class BeautyLevelPowerFragment : BaseFragment() {

    override fun init() {

    }

    override fun setContentViewId(): Int {
        return R.layout.fragment_meilizhi_level_power
    }

    override fun setUpView() {

    }

    companion object {

        var inst: BeautyLevelPowerFragment? = null
        fun getInstance(): BeautyLevelPowerFragment {
            if (inst == null) {
                inst = BeautyLevelPowerFragment()
            }
            return inst!!
        }
    }
}
