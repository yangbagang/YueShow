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
import com.ybg.yxym.yueshow.view.pickerview.lib.WheelOptions

import java.util.ArrayList

class OptionsPopupWindow(context: Context) : PopupWindow(context), OnClickListener {
    private val rootView: View // 总的布局
    internal var wheelOptions: WheelOptions
    private val btnSubmit: View
    private val btnCancel: View
    private var optionsSelectListener: OnOptionsSelectListener? = null

    init {
        this.width = LayoutParams.FILL_PARENT
        this.height = LayoutParams.WRAP_CONTENT
        this.setBackgroundDrawable(BitmapDrawable())// 这样设置才能点击屏幕外dismiss窗口
        this.isOutsideTouchable = true
        this.animationStyle = R.style.ActionSheetDialogAnimation

        val mLayoutInflater = LayoutInflater.from(context)
        rootView = mLayoutInflater.inflate(R.layout.pw_options, null)
        // -----确定和取消按钮
        btnSubmit = rootView.findViewById(R.id.btnSubmit)
        btnSubmit.tag = TAG_SUBMIT
        btnCancel = rootView.findViewById(R.id.btnCancel)
        btnCancel.tag = TAG_CANCEL
        btnSubmit.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        // ----转轮
        val optionspicker = rootView.findViewById(R.id.optionspicker)
        val screenInfo = ScreenInfo(context as Activity)
        wheelOptions = WheelOptions(optionspicker)

        wheelOptions.screenheight = screenInfo.height

        contentView = rootView
    }

    fun setPicker(optionsItems: ArrayList<String>) {
        wheelOptions.setPicker(optionsItems, null, null, null, false)
    }

    fun setPicker(options1Items: ArrayList<String>,
                  options2Items: ArrayList<String>, linkage: Boolean) {
        wheelOptions.setPicker(options1Items, options2Items, null, null, linkage)
    }

    fun setPicker(options1Items: ArrayList<String>,
                  options2Items: ArrayList<String>,
                  options3Items: ArrayList<String>,
                  options4Items: ArrayList<String>,
                  linkage: Boolean) {
        wheelOptions.setPicker(options1Items, options2Items, options3Items, options4Items,
                linkage)
    }

    /**
     * 设置选中的item位置

     * @param option1
     */
    fun setSelectOptions(option1: Int) {
        wheelOptions.setCurrentItems(option1, 0, 0, 0)
    }

    /**
     * 设置选中的item位置

     * @param option1
     * *
     * @param option2
     */
    fun setSelectOptions(option1: Int, option2: Int) {
        wheelOptions.setCurrentItems(option1, option2, 0, 0)
    }

    /**
     * 设置选中的item位置

     * @param option1
     * *
     * @param option2
     * *
     * @param option3
     */
    fun setSelectOptions(option1: Int, option2: Int, option3: Int) {
        wheelOptions.setCurrentItems(option1, option2, option3, 0)
    }

    /**
     * 设置选中的item位置

     * @param option1
     * *
     * @param option2
     * *
     * @param option3
     */
    fun setSelectOptions(option1: Int, option2: Int, option3: Int, option4: Int) {
        wheelOptions.setCurrentItems(option1, option2, option3, option4)
    }


    /**
     * 设置选项的单位

     * @param label1
     */
    fun setLabels(label1: String) {
        wheelOptions.setLabels(label1, null, null, null)
    }

    /**
     * 设置选项的单位

     * @param label1
     * *
     * @param label2
     */
    fun setLabels(label1: String, label2: String) {
        wheelOptions.setLabels(label1, label2, null, null)
    }

    /**
     * 设置选项的单位

     * @param label1
     * *
     * @param label2
     * *
     * @param label3
     */
    fun setLabels(label1: String, label2: String, label3: String) {
        wheelOptions.setLabels(label1, label2, label3, null)
    }

    /**
     * 设置选项的单位

     * @param label1
     * *
     * @param label2
     * *
     * @param label3
     */
    fun setLabels(label1: String, label2: String, label3: String, label4: String) {
        wheelOptions.setLabels(label1, label2, label3, label4)
    }

    /**
     * 设置是否循环滚动

     * @param cyclic
     */
    fun setCyclic(cyclic: Boolean) {
        wheelOptions.setCyclic(cyclic)
    }

    override fun onClick(v: View) {
        val tag = v.tag as String
        if (tag == TAG_CANCEL) {
            dismiss()
            return
        } else {
            if (optionsSelectListener != null) {
                val optionsCurrentItems = wheelOptions.currentItems
                optionsSelectListener!!.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2], optionsCurrentItems[3])
            }
            dismiss()
            return
        }
    }

    interface OnOptionsSelectListener {
        fun onOptionsSelect(options1: Int, option2: Int, options3: Int, options4: Int)
    }

    fun setOnoptionsSelectListener(
            optionsSelectListener: OnOptionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener
    }

    companion object {
        private val TAG_SUBMIT = "submit"
        private val TAG_CANCEL = "cancel"
    }
}
