package com.ybg.yxym.yueshow.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Scroller

class PullScrollView : ScrollView {
    private val mScroller: Scroller
    internal var tool: TouchTool? = null
    internal var left: Int = 0
    internal var top: Int = 0
    internal var startX: Float = 0.toFloat()
    internal var startY: Float = 0.toFloat()
    internal var currentX: Float = 0.toFloat()
    internal var currentY: Float = 0.toFloat()
    internal var imageViewH: Int = 0
    internal var rootW: Int = 0
    internal var rootH: Int = 0
    internal var imageView: ImageView? = null
    internal var scrollerType: Boolean = false

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mScroller = Scroller(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mScroller = Scroller(context)
    }

    constructor(context: Context) : super(context) {
        mScroller = Scroller(context)
    }

    fun setImageView(imageView: ImageView) {
        this.imageView = imageView
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (!mScroller.isFinished) {
            return super.onTouchEvent(event)
        }
        if (imageView == null) {
            return super.onTouchEvent(event)
        }
        currentX = event.x
        currentY = event.y
        imageView!!.top
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                left = imageView!!.left
                top = imageView!!.bottom
                rootW = width
                rootH = height
                imageViewH = imageView!!.height
                startX = currentX
                startY = currentY
                tool = TouchTool(imageView!!.left, imageView!!.bottom,
                        imageView!!.left, imageView!!.bottom + LEN)
            }
            MotionEvent.ACTION_MOVE -> if (imageView!!.isShown && imageView!!.top >= 0) {
                if (tool != null) {
                    val t = tool!!.getScrollY(currentY - startY)
                    if (t >= top && t <= imageView!!.bottom + LEN) {
                        val params = imageView!!.layoutParams
                        params.height = t
                        imageView!!.layoutParams = params
                    }
                }
                scrollerType = false
            }
            MotionEvent.ACTION_UP -> {
                scrollerType = true
                mScroller.startScroll(imageView!!.left, imageView!!.bottom,
                        0 - imageView!!.left,
                        imageViewH - imageView!!.bottom, DURATION)
                invalidate()
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val x = mScroller.currX
            val y = mScroller.currY
            imageView!!.layout(0, 0, x + imageView!!.width, y)
            invalidate()
            if (!mScroller.isFinished && scrollerType && y > MAX_DY) {
                val params = imageView!!.layoutParams
                params.height = y
                imageView!!.layoutParams = params
            }
        }
    }

    inner class TouchTool(private val startX: Int, private val startY: Int, endX: Int, endY: Int) {

        fun getScrollX(dx: Float): Int {
            val xx = (startX + dx / 2.5f).toInt()
            return xx
        }

        fun getScrollY(dy: Float): Int {
            val yy = (startY + dy / 2.5f).toInt()
            return yy
        }
    }

    companion object {
        private val LEN = 0xc8
        private val DURATION = 500
        private val MAX_DY = 200
    }
}
