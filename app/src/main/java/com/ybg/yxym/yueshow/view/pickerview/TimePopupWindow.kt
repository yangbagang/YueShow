package com.ybg.yxym.yueshow.view.pickerview

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.widget.PopupWindow

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.view.pickerview.lib.ScreenInfo
import com.ybg.yxym.yueshow.view.pickerview.lib.WheelTime

import java.text.ParseException
import java.util.Calendar
import java.util.Date

class TimePopupWindow(context: Context, type: TimePopupWindow.Type) : PopupWindow(context), OnClickListener {
    enum class Type {
        ALL, YEAR_MONTH_DAY, HOURS_MINS, MONTH_DAY_HOUR_MIN
    }// 四种选择模式，年月日时分，年月日，时分，月日时分

    private val rootView: View // 总的布局
    internal var wheelTime: WheelTime
    private val btnSubmit: View
    private val btnCancel: View
    private var timeSelectListener: OnTimeSelectListener? = null

    init {
        this.width = LayoutParams.FILL_PARENT
        this.height = LayoutParams.WRAP_CONTENT
        this.setBackgroundDrawable(BitmapDrawable())// 这样设置才能点击屏幕外dismiss窗口
        this.isOutsideTouchable = true
        this.animationStyle = R.style.ActionSheetDialogAnimation

        val mLayoutInflater = LayoutInflater.from(context)
        rootView = mLayoutInflater.inflate(R.layout.pw_time, null)
        // -----确定和取消按钮
        btnSubmit = rootView.findViewById(R.id.btnSubmit)
        btnSubmit.tag = TAG_SUBMIT
        btnCancel = rootView.findViewById(R.id.btnCancel)
        btnCancel.tag = TAG_CANCEL
        btnSubmit.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        // ----时间转轮
        val timepickerview = rootView.findViewById(R.id.timepicker)
        val screenInfo = ScreenInfo(context as Activity)
        wheelTime = WheelTime(timepickerview, type)

        wheelTime.screenheight = screenInfo.height

        //默认选中当前时间
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        wheelTime.setPicker(year, month, day, hours, minute)

        contentView = rootView
    }

    /**
     * 设置可以选择的时间范围

     * @param START_YEAR
     * *
     * @param END_YEAR
     */
    fun setRange(START_YEAR: Int, END_YEAR: Int) {
        WheelTime.starT_YEAR = START_YEAR
        WheelTime.enD_YEAR = END_YEAR
    }

    /**
     * 设置选中时间

     * @param date
     */
    fun setTime(date: Date?) {
        val calendar = Calendar.getInstance()
        if (date == null)
            calendar.timeInMillis = System.currentTimeMillis()
        else
            calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        wheelTime.setPicker(year, month, day, hours, minute)
    }

    /**
     * 指定选中的时间，显示选择器

     * @param parent
     * *
     * @param gravity
     * *
     * @param x
     * *
     * @param y
     * *
     * @param date
     */
    fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int, date: Date?) {
        val calendar = Calendar.getInstance()
        if (date == null)
            calendar.timeInMillis = System.currentTimeMillis()
        else
            calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        wheelTime.setPicker(year, month, day, hours, minute)
        update()
        super.showAtLocation(parent, gravity, x, y)
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        wheelTime.setPicker(year, month, day, hours, minute)
        update()
        super.showAtLocation(parent, gravity, x, y)
    }

    /**
     * 设置是否循环滚动

     * @param cyclic
     */
    fun setCyclic(cyclic: Boolean) {
        wheelTime.setCyclic(cyclic)
    }

    override fun onClick(v: View) {
        val tag = v.tag as String
        if (tag == TAG_CANCEL) {
            dismiss()
            return
        } else {
            if (timeSelectListener != null) {
                try {
                    val date = WheelTime.dateFormat.parse(wheelTime.time)
                    timeSelectListener!!.onTimeSelect(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
            dismiss()
            return
        }
    }

    interface OnTimeSelectListener {
        fun onTimeSelect(date: Date)
    }

    fun setOnTimeSelectListener(timeSelectListener: OnTimeSelectListener) {
        this.timeSelectListener = timeSelectListener
    }

    companion object {
        private val TAG_SUBMIT = "submit"
        private val TAG_CANCEL = "cancel"
    }

}
