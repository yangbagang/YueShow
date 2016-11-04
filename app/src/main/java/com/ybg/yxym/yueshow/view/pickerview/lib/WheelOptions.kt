package com.ybg.yxym.yueshow.view.pickerview.lib

import android.view.View


import com.ybg.yxym.yueshow.R

import java.util.ArrayList

class WheelOptions(var view: View?) {
    private var wv_option1: WheelView? = null
    private var wv_option2: WheelView? = null
    private var wv_option3: WheelView? = null
    private var wv_option4: WheelView? = null

    private var mOptions1Items: ArrayList<String>? = null
    private var mOptions2Items: ArrayList<String>? = null
    private var mOptions3Items: ArrayList<String>? = null
    private var mOptions4Items: ArrayList<String>? = null


    var screenheight: Int = 0

    init {
        view = view
    }

    fun setPicker(optionsItems: ArrayList<String>) {
        setPicker(optionsItems, null, null, null, false)
    }

    fun setPicker(options1Items: ArrayList<String>,
                  options2Items: ArrayList<String>, linkage: Boolean) {
        setPicker(options1Items, options2Items, null, null, linkage)
    }

    fun setPicker(options1Items: ArrayList<String>,
                  options2Items: ArrayList<String>,
                  options3Items: ArrayList<String>,
                  linkage: Boolean) {
        setPicker(options1Items, options2Items, options3Items, null, linkage)
    }

    fun setPicker(options1Items: ArrayList<String>,
                  options2Items: ArrayList<String>?,
                  options3Items: ArrayList<String>?,
                  options4Items: ArrayList<String>?,
                  linkage: Boolean) {
        this.mOptions1Items = options1Items
        this.mOptions2Items = options2Items
        this.mOptions3Items = options3Items
        this.mOptions4Items = options4Items

        var len = ArrayWheelAdapter.DEFAULT_LENGTH

        if (this.mOptions3Items == null)
            len = 8
        if (this.mOptions2Items == null)
            len = 12


        // 选项1
        wv_option1 = view!!.findViewById(R.id.options1) as WheelView
        wv_option1!!.adapter = ArrayWheelAdapter(mOptions1Items!!, len)// 设置显示数据
        wv_option1!!.currentItem = 0// 初始化时显示的数据
        // 选项2
        wv_option2 = view!!.findViewById(R.id.options2) as WheelView
        if (mOptions2Items != null)
            wv_option2!!.adapter = ArrayWheelAdapter(mOptions2Items!!)// 设置显示数据
        wv_option2!!.currentItem = 0// 初始化时显示的数据
        // 选项3
        wv_option3 = view!!.findViewById(R.id.options3) as WheelView
        if (mOptions3Items != null)
            wv_option3!!.adapter = ArrayWheelAdapter(mOptions3Items!!)// 设置显示数据
        wv_option3!!.currentItem = 0// 初始化时显示的数据

        wv_option4 = view!!.findViewById(R.id.options4) as WheelView
        if (mOptions4Items != null)
            wv_option4!!.adapter = ArrayWheelAdapter(mOptions4Items!!)// 设置显示数据
        wv_option4!!.currentItem = 0// 初始化时显示的数据

        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
        val textSize = screenheight / 200 * 4

        wv_option1!!.TEXT_SIZE = textSize
        wv_option2!!.TEXT_SIZE = textSize
        wv_option3!!.TEXT_SIZE = textSize
        wv_option4!!.TEXT_SIZE = textSize

        if (this.mOptions2Items == null)
            wv_option2!!.visibility = View.GONE
        if (this.mOptions3Items == null)
            wv_option3!!.visibility = View.GONE
        if (this.mOptions4Items == null)
            wv_option4!!.visibility = View.GONE
    }

    /**
     * 设置选项的单位

     * @param label1
     * *
     * @param label2
     * *
     * @param label3
     */
    fun setLabels(label1: String?, label2: String?, label3: String?, label4: String?) {
        if (label1 != null)
            wv_option1!!.label = label1
        if (label2 != null)
            wv_option2!!.label = label2
        if (label3 != null)
            wv_option3!!.label = label3
        if (label4 != null)
            wv_option4!!.label = label4
    }

    /**
     * 设置是否循环滚动

     * @param cyclic
     */
    fun setCyclic(cyclic: Boolean) {
        wv_option1!!.setCyclic(cyclic)
        wv_option2!!.setCyclic(cyclic)
        wv_option3!!.setCyclic(cyclic)
        wv_option4!!.setCyclic(cyclic)
    }

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2

     * @return
     */
    val currentItems: IntArray
        get() {
            val currentItems = IntArray(4)
            currentItems[0] = wv_option1!!.currentItem
            currentItems[1] = wv_option2!!.currentItem
            currentItems[2] = wv_option3!!.currentItem
            currentItems[3] = wv_option4!!.currentItem
            return currentItems
        }

    fun setCurrentItems(option1: Int, option2: Int, option3: Int, option4: Int) {
        wv_option1!!.currentItem = option1
        wv_option2!!.currentItem = option2
        wv_option3!!.currentItem = option3
        wv_option4!!.currentItem = option4
    }
}
