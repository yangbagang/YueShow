package com.ybg.yxym.yueshow.view.pickerview.lib

import android.app.Activity
import android.util.DisplayMetrics

/**
 * 得到屏幕宽高密度等
 */
class ScreenInfo(var activity: Activity?) {
    /** 屏幕宽度（像素） */
    var width: Int = 0
    /**屏幕高度（像素） */
    var height: Int = 0
    /**屏幕密度（0.75 / 1.0 / 1.5） */
    var density: Float = 0.toFloat()
    /**屏幕密度DPI（120 / 160 / 240） */
    var densityDpi: Int = 0

    init {
        ini()
    }

    private fun ini() {
        val metric = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metric)
        width = metric.widthPixels
        height = metric.heightPixels
        density = metric.density
        densityDpi = metric.densityDpi
    }


}
