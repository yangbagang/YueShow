package com.ybg.yxym.yueshow.activity.bang

import com.ybg.yxym.yb.utils.DateUtil
import java.util.*

/**
 * Created by yangbagang on 2016/12/2.
 */
class DayBangFragment : BangBaseFragment() {

    override fun initParams() {
        val today = DateUtil.getDate(Date())
        beginTime = "$today 00:00:00"
        endTime = "$today 23:59:59"
    }

    override var type = 1
    override var beginTime = ""
    override var endTime = ""

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