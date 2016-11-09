package com.ybg.yxym.yueshow.utils

import android.content.Context
import android.view.Gravity
import android.view.View

import com.ybg.yxym.yueshow.view.pickerview.OptionsPopupWindow
import com.ybg.yxym.yueshow.view.pickerview.TimePopupWindow

import java.util.ArrayList

/**
 * 底部选择弹窗的工具类封装
 */
object OnoptionsUtils {

    /**
     * 选择性别的弹窗部分
     */
    fun showGardenSelect(context: Context, view: View, listener: OptionsPopupWindow.OnOptionsSelectListener) {
        val picker = OptionsPopupWindow(context)
        val list = ArrayList<String>()
        list.add("女")
        list.add("男")
        picker.setPicker(list)
        picker.setOnoptionsSelectListener(listener)
        picker.showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 选择生日
     *
     * @param context
     * @param view
     *
     * @param listener
     */
    fun showDateSelect(context: Context, view: View, listener: TimePopupWindow.OnTimeSelectListener) {
        val pop = TimePopupWindow(context, TimePopupWindow.Type.YEAR_MONTH_DAY)
        pop.setOnTimeSelectListener(listener)
        pop.showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 选择身高
     */
    fun showheight(context: Context, view: View, listener: OptionsPopupWindow.OnOptionsSelectListener) {
        val picker = OptionsPopupWindow(context)
        picker.setPicker(getData(0))
        picker.setSelectOptions(160)
        picker.setLabels("身高")
        picker.setOnoptionsSelectListener(listener)
        picker.showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 选择体重
     */
    fun showWeight(context: Context, view: View, listener: OptionsPopupWindow.OnOptionsSelectListener) {
        val picker = OptionsPopupWindow(context)
        picker.setPicker(getData(1))
        picker.setSelectOptions(50)
        picker.setLabels("体重")
        picker.setOnoptionsSelectListener(listener)
        picker.showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }


    /**
     * 选择身材
     */
    fun showShencai(context: Context, view: View, listener: OptionsPopupWindow.OnOptionsSelectListener) {
        val picker = OptionsPopupWindow(context)
        picker.setPicker(getData(2), getData(3), getData(4), getData(5), false)
        picker.setSelectOptions(1, 90, 70, 80)
        picker.setLabels("罩杯", "胸围", "腰围", "臀围")
        picker.setOnoptionsSelectListener(listener)
        picker.showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }

    /**
     * @return 获取数据
     */
    private fun getData(type: Int): ArrayList<String> {
        val list = ArrayList<String>()
        if (type == 0) {
            for (i in 0..229) {
                list.add("${i}cm")
            }
        } else if (type == 1) {
            for (i in 0..149) {
                list.add("${i}kg")
            }
        } else if (type == 2) {
            for (i in 0..7) {
                list.add(cups[i])
            }
        } else if (type == 3) {
            for (i in 0..149) {
                list.add("${i}")
            }
        } else if (type == 4) {
            for (i in 0..149) {
                list.add("${i}")
            }
        } else if (type == 5) {
            for (i in 0..149) {
                list.add("${i}")
            }
        }
        return list
    }

    var cups = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
}
