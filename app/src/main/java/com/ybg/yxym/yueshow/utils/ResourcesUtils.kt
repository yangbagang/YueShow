package com.ybg.yxym.yueshow.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.view.View

import com.ybg.yxym.yueshow.app.ShowApplication

object ResourcesUtils {

    val context: Context
        get() = ShowApplication.instance!!

    /**
     * dip转换px
     */
    fun dip2px(dip: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dip * scale + 0.5f).toInt()
    }

    /**
     * px转换dip
     */
    fun px2dip(px: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }

    /**
     * 获取资源
     */
    val resources: Resources
        get() = context.resources

    /**
     * 获取文字
     */
    fun getString(resId: Int): String {
        return resources.getString(resId)
    }

    /**
     * 获取文字数组
     */
    fun getStringArray(resId: Int): Array<String> {
        return resources.getStringArray(resId)
    }

    /**
     * 获取dimen
     */
    fun getDimens(resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    /**
     * 获取drawable
     */
    fun getDrawable(resId: Int): Drawable {
        return resources.getDrawable(resId)
    }

    /**
     * 获取颜色
     */
    fun getColor(resId: Int): Int {
        return resources.getColor(resId)
    }

    /**
     * 获取颜色选择器
     */
    fun getColorStateList(resId: Int): ColorStateList {
        return resources.getColorStateList(resId)
    }

}


