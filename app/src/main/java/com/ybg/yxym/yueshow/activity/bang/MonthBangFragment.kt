package com.ybg.yxym.yueshow.activity.bang

import com.ybg.yxym.yb.utils.DateUtil
import java.util.*

/**
 * Created by yangbagang on 2016/12/2.
 */
class MonthBangFragment : BangBaseFragment () {

    override fun initParams() {
        val today = DateUtil.getDate(Date())
        beginTime = "${DateUtil.getMonthBegin(today)} 00:00:00"
        endTime = "${DateUtil.getMonthEnd(today)} 23:59:59"
    }

    override var type = 1
    override var beginTime = ""
    override var endTime = ""

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