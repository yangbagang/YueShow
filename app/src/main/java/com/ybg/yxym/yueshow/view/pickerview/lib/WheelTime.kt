package com.ybg.yxym.yueshow.view.pickerview.lib

import android.content.Context
import android.view.View

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.view.pickerview.TimePopupWindow

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays

class WheelTime {
    var view: View? = null
    private var wv_year: WheelView? = null
    private var wv_month: WheelView? = null
    private var wv_day: WheelView? = null
    private var wv_hours: WheelView? = null
    private var wv_mins: WheelView? = null
    var screenheight: Int = 0

    private var type: TimePopupWindow.Type? = null

    constructor(view: View) : super() {
        this.view = view
        type = TimePopupWindow.Type.ALL
    }

    constructor(view: View, type: TimePopupWindow.Type) : super() {
        this.view = view
        this.type = type
    }

    fun setPicker(year: Int, month: Int, day: Int) {
        this.setPicker(year, month, day, 0, 0)
    }

    /**
     * @Description: 弹出日期时间选择器
     */
    fun setPicker(year: Int, month: Int, day: Int, h: Int, m: Int) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        val months_big = arrayOf("1", "3", "5", "7", "8", "10", "12")
        val months_little = arrayOf("4", "6", "9", "11")

        val list_big = Arrays.asList(*months_big)
        val list_little = Arrays.asList(*months_little)

        val context = view!!.context
        // 年
        wv_year = view!!.findViewById(R.id.year) as WheelView
        wv_year!!.adapter = NumericWheelAdapter(starT_YEAR, enD_YEAR)// 设置"年"的显示数据
        wv_year!!.label = context.getString(R.string.pickerview_year)// 添加文字
        wv_year!!.currentItem = year - starT_YEAR// 初始化时显示的数据

        // 月
        wv_month = view!!.findViewById(R.id.month) as WheelView
        wv_month!!.adapter = NumericWheelAdapter(1, 12)
        wv_month!!.label = context.getString(R.string.pickerview_month)
        wv_month!!.currentItem = month

        // 日
        wv_day = view!!.findViewById(R.id.day) as WheelView
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains((month + 1).toString())) {
            wv_day!!.adapter = NumericWheelAdapter(1, 31)
        } else if (list_little.contains((month + 1).toString())) {
            wv_day!!.adapter = NumericWheelAdapter(1, 30)
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                wv_day!!.adapter = NumericWheelAdapter(1, 29)
            else
                wv_day!!.adapter = NumericWheelAdapter(1, 28)
        }
        wv_day!!.label = context.getString(R.string.pickerview_day)
        wv_day!!.currentItem = day - 1

        wv_hours = view!!.findViewById(R.id.hour) as WheelView
        wv_hours!!.adapter = NumericWheelAdapter(0, 23)
        wv_hours!!.label = context.getString(R.string.pickerview_hours)// 添加文字
        wv_hours!!.currentItem = h

        wv_mins = view!!.findViewById(R.id.min) as WheelView
        wv_mins!!.adapter = NumericWheelAdapter(0, 59)
        wv_mins!!.label = context.getString(R.string.pickerview_minutes)// 添加文字
        wv_mins!!.currentItem = m

        // 添加"年"监听
        val wheelListener_year = object : OnWheelChangedListener {
            override fun onChanged(wheel: WheelView, oldValue: Int, newValue: Int) {
                val year_num = newValue + starT_YEAR
                // 判断大小月及是否闰年,用来确定"日"的数据
                var maxItem = 30
                if (list_big.contains((wv_month!!.currentItem + 1).toString())) {
                    wv_day!!.adapter = NumericWheelAdapter(1, 31)
                    maxItem = 31
                } else if (list_little.contains((wv_month!!.currentItem + 1).toString())) {
                    wv_day!!.adapter = NumericWheelAdapter(1, 30)
                    maxItem = 30
                } else {
                    if (year_num % 4 == 0 && year_num % 100 != 0 || year_num % 400 == 0) {
                        wv_day!!.adapter = NumericWheelAdapter(1, 29)
                        maxItem = 29
                    } else {
                        wv_day!!.adapter = NumericWheelAdapter(1, 28)
                        maxItem = 28
                    }
                }
                if (wv_day!!.currentItem > maxItem - 1) {
                    wv_day!!.currentItem = maxItem - 1
                }
            }
        }
        // 添加"月"监听
        val wheelListener_month = object : OnWheelChangedListener {
            override fun onChanged(wheel: WheelView, oldValue: Int, newValue: Int) {
                val month_num = newValue + 1
                var maxItem = 30
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(month_num.toString())) {
                    wv_day!!.adapter = NumericWheelAdapter(1, 31)
                    maxItem = 31
                } else if (list_little.contains(month_num.toString())) {
                    wv_day!!.adapter = NumericWheelAdapter(1, 30)
                    maxItem = 30
                } else {
                    if ((wv_year!!.currentItem + starT_YEAR) % 4 == 0 && (wv_year!!.currentItem + starT_YEAR) % 100 != 0 || (wv_year!!.currentItem + starT_YEAR) % 400 == 0) {
                        wv_day!!.adapter = NumericWheelAdapter(1, 29)
                        maxItem = 29
                    } else {
                        wv_day!!.adapter = NumericWheelAdapter(1, 28)
                        maxItem = 28
                    }
                }
                if (wv_day!!.currentItem > maxItem - 1) {
                    wv_day!!.currentItem = maxItem - 1
                }

            }
        }
        wv_year!!.addChangingListener(wheelListener_year)
        wv_month!!.addChangingListener(wheelListener_month)

        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
        var textSize = 0
        when (type) {
            TimePopupWindow.Type.ALL -> textSize = screenheight / 200 * 3
            TimePopupWindow.Type.YEAR_MONTH_DAY -> {
                textSize = screenheight / 200 * 4
                wv_hours!!.visibility = View.GONE
                wv_mins!!.visibility = View.GONE
            }
            TimePopupWindow.Type.HOURS_MINS -> {
                textSize = screenheight / 200 * 4
                wv_year!!.visibility = View.GONE
                wv_month!!.visibility = View.GONE
                wv_day!!.visibility = View.GONE
            }
            TimePopupWindow.Type.MONTH_DAY_HOUR_MIN -> {
                textSize = screenheight / 200 * 3
                wv_year!!.visibility = View.GONE
            }
        }

        wv_day!!.TEXT_SIZE = textSize
        wv_month!!.TEXT_SIZE = textSize
        wv_year!!.TEXT_SIZE = textSize
        wv_hours!!.TEXT_SIZE = textSize
        wv_mins!!.TEXT_SIZE = textSize

    }

    /**
     * 设置是否循环滚动

     * @param cyclic
     */
    fun setCyclic(cyclic: Boolean) {
        wv_year!!.setCyclic(cyclic)
        wv_month!!.setCyclic(cyclic)
        wv_day!!.setCyclic(cyclic)
        wv_hours!!.setCyclic(cyclic)
        wv_mins!!.setCyclic(cyclic)
    }

    val time: String
        get() {
            val sb = StringBuffer()
            sb.append(wv_year!!.currentItem + starT_YEAR).append("-").append(wv_month!!.currentItem + 1).append("-").append(wv_day!!.currentItem + 1).append(" ").append(wv_hours!!.currentItem).append(":").append(wv_mins!!.currentItem)
            return sb.toString()
        }

    companion object {
        var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var starT_YEAR = 1990
        var enD_YEAR = 2100
    }
}
