package com.ybg.yxym.yueshow.view.pickerview.lib

import android.content.Context

object DensityUtil {
    private var scale: Float = 0.toFloat()

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        if (scale == 0f)
            scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        if (scale == 0f)
            scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}
