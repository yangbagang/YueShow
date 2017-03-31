package com.ybg.yxym.yueshow.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by yangbagang on 2017/3/30.
 */

class ContactSideBar : View {

    // 触摸事件
    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private var choose = -1// 选中
    private val paint = Paint()

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    /**
     * 重写这个方法
     */
    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 获取焦点改变背景颜色.
        val height = getHeight()// 获取对应高度
        val width = getWidth() // 获取对应宽度
        val singleHeight = height / A_Z.size - 2// 获取每一个字母的高度  (这里-2仅仅是为了好看而已)
        for (i in A_Z.indices) {
            paint.setColor(Color.rgb(4 * 16 + 13, 5 * 16 + 3, 5 * 16 + 9))  //设置字体颜色
            paint.setTypeface(Typeface.DEFAULT_BOLD)  //设置字体
            paint.setAntiAlias(true)  //设置抗锯齿
            paint.setTextSize(20f)  //设置字母字体大小
            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"))  //选中的字母改变颜色
                paint.setFakeBoldText(true)  //设置字体为粗体
            }
            // x坐标等于中间-字符串宽度的一半.
            val xPos = width / 2 - paint.measureText(A_Z[i]) / 2
            val yPos = (singleHeight * i + singleHeight).toFloat()
            canvas.drawText(A_Z[i], xPos, yPos, paint)  //绘制所有的字母
            paint.reset()// 重置画笔
        }
    }

    public override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.getAction()
        val y = event.getY()// 点击y坐标
        val oldChoose = choose
        val listener = onTouchingLetterChangedListener
        val c = (y / getHeight() * A_Z.size).toInt()// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        when (action) {
            MotionEvent.ACTION_UP -> {
                setBackgroundDrawable(ColorDrawable(0x00000000))
                choose = -1//
                invalidate()
            }
            else ->
                //setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {  //判断选中字母是否发生改变
                    if (c >= 0 && c < A_Z.size) {
                        if (listener != null) {
                            listener!!.onTouchingLetterChanged(A_Z[c])
                        }
                        choose = c
                        invalidate()
                    }
                }
        }
        return true
    }

    /**
     * 向外公开的方法
     */
    fun setOnTouchingLetterChangedListener(
            onTouchingLetterChangedListener: OnTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    /**
     * 接口

     * @author coder
     */
    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String)
    }

    companion object {
        // 26个字母
        var A_Z = arrayOf<String>("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
    }

}
