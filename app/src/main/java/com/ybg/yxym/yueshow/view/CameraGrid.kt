package com.ybg.yxym.yueshow.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * 照相机井字线
 */
class CameraGrid @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var topWidth = 0
        private set
    private var mPaint: Paint? = null

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.color = Color.WHITE
        mPaint!!.alpha = 120
        mPaint!!.strokeWidth = 1f
    }


    //画一个井字,上下画两条灰边，中间为正方形
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = this.width
        val height = this.height
        if (width < height) {
            topWidth = height - width
        }
        if (isShowGrid) {
            canvas.drawLine((width / 3).toFloat(), 0f, (width / 3).toFloat(), height.toFloat(), mPaint!!)
            canvas.drawLine((width * 2 / 3).toFloat(), 0f, (width * 2 / 3).toFloat(), height.toFloat(), mPaint!!)
            canvas.drawLine(0f, (height / 3).toFloat(), width.toFloat(), (height / 3).toFloat(), mPaint!!)
            canvas.drawLine(0f, (height * 2 / 3).toFloat(), width.toFloat(), (height * 2 / 3).toFloat(), mPaint!!)
        }
    }

    var isShowGrid = true
}
