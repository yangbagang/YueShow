package com.ybg.yxym.yueshow.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ScrollView
import android.widget.TextView

import com.ybg.yxym.yueshow.R

import java.util.ArrayList

class ActionSheet(private val context: Context) {
    private var dialog: Dialog? = null
    private var txt_title: TextView? = null
    private var txt_cancel: TextView? = null
    private var lLayout_content: LinearLayout? = null
    private var sLayout_content: ScrollView? = null
    private var showTitle = false
    private var sheetItemList: MutableList<SheetItem>? = null
    private val display: Display

    init {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        display = windowManager.defaultDisplay
    }

    fun builder(): ActionSheet {
        // 获取Dialog布局
        val view = LayoutInflater.from(context).inflate(
                R.layout.view_actionsheet, null)

        // 设置Dialog最小宽度为屏幕宽度
        view.minimumWidth = display.width

        // 获取自定义Dialog布局中的控件
        sLayout_content = view.findViewById(R.id.sLayout_content) as ScrollView
        lLayout_content = view.findViewById(R.id.lLayout_content) as LinearLayout
        txt_title = view.findViewById(R.id.txt_title) as TextView
        txt_cancel = view.findViewById(R.id.txt_cancel) as TextView
        txt_cancel!!.setOnClickListener { dialog!!.dismiss() }

        // 定义Dialog布局和参数
        dialog = Dialog(context, R.style.ActionSheetDialogStyle)
        dialog!!.setContentView(view)
        val dialogWindow = dialog!!.window
        dialogWindow!!.setGravity(Gravity.LEFT or Gravity.BOTTOM)
        val lp = dialogWindow.attributes
        lp.x = 0
        lp.y = 0
        dialogWindow.attributes = lp

        return this
    }

    fun setTitle(title: String): ActionSheet {
        showTitle = true
        txt_title!!.visibility = View.VISIBLE
        txt_title!!.text = title
        return this
    }

    fun setCancelable(cancel: Boolean): ActionSheet {
        dialog!!.setCancelable(cancel)
        return this
    }

    fun setCanceledOnTouchOutside(cancel: Boolean): ActionSheet {
        dialog!!.setCanceledOnTouchOutside(cancel)
        return this
    }

    /**
     * @param strItem  条目名称
     * *
     * @param color    条目字体颜色，设置null则默认蓝色
     * *
     * @param listener
     * *
     * @return
     */
    fun addSheetItem(strItem: String, color: SheetItemColor,
                     listener: OnSheetItemClickListener): ActionSheet {
        if (sheetItemList == null) {
            sheetItemList = ArrayList<SheetItem>()
        }
        sheetItemList!!.add(SheetItem(strItem, color, listener))
        return this
    }

    /**
     * 设置条目布局
     */
    private fun setSheetItems() {
        if (sheetItemList == null || sheetItemList!!.size <= 0) {
            return
        }

        val size = sheetItemList!!.size

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            val params = sLayout_content!!.layoutParams as LayoutParams
            params.height = display.height / 2
            sLayout_content!!.layoutParams = params
        }

        // 循环添加条目
        for (i in 1..size) {
            val index = i
            val sheetItem = sheetItemList!![i - 1]
            val strItem = sheetItem.name
            val color = sheetItem.color

            val textView = TextView(context)
            textView.text = strItem
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER

            // 背景图片
            if (size == 1) {
                if (showTitle) {
                    textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector)
                } else {
                    textView.setBackgroundResource(R.drawable.actionsheet_single_selector)
                }
            } else {
                if (showTitle) {
                    if (i >= 1 && i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector)
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector)
                    }
                } else {
                    if (i == 1) {
                        textView.setBackgroundResource(R.drawable.actionsheet_top_selector)
                    } else if (i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector)
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector)
                    }
                }
            }

            // 字体颜色
            if (color == null) {
                textView.setTextColor(Color.parseColor(SheetItemColor.Blue.name))
            } else {
                textView.setTextColor(Color.parseColor(color.name))
            }

            // 高度
            val scale = context.resources.displayMetrics.density
            val height = (45 * scale + 0.5f).toInt()
            textView.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT, height)

            // 点击事件
            textView.setOnClickListener {
                sheetItem.itemClickListener.onClick(index)
                dialog!!.dismiss()
            }

            lLayout_content!!.addView(textView)
        }
    }

    fun show() {
        setSheetItems()
        dialog!!.show()
    }

    interface OnSheetItemClickListener {
        fun onClick(which: Int)
    }

    inner class SheetItem(internal var name: String, internal var color: SheetItemColor?,
                          internal var itemClickListener: OnSheetItemClickListener)

    enum class SheetItemColor private constructor(var color: String?) {
        Blue("#037BFF"), Red("#FD4A2E")
    }
}
