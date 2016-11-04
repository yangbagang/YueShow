package com.ybg.yxym.yueshow.view.gallery.view

import android.content.Context
import android.graphics.Matrix
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration
import android.widget.ImageView

/**
 * 支持手势的ImageView
 * Created by Nereo on 2015/4/10.
 */
class GestureImageView : ImageView {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private var mScaleGesture: ScaleGestureDetector? = null
    private var mImageMatrix: Matrix? = null
    private var mGestureDetector: GestureDetectorCompat? = null
    // 系统常量，系统认为手指是否移动的最小距离
    private var mTouchSlop: Int = 0

    private val mCurrentFactor = 1.0f

    private val mFirstPointerX: Float = 0.toFloat()
    private val mFirstPointerY: Float = 0.toFloat()
    private val mSecondPointerX: Float = 0.toFloat()
    private val mSecondPointerY: Float = 0.toFloat()

    private var mCenterX: Int = 0
    private var mCenterY: Int = 0

    /**
     * 初始化
     * @param context
     */
    private fun init(context: Context) {

        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        scaleType = ImageView.ScaleType.MATRIX

        mImageMatrix = Matrix()

        mScaleGesture = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val factor = detector.scaleFactor
                mImageMatrix!!.postScale(factor, factor, mCenterX.toFloat(), mCenterY.toFloat())
                imageMatrix = mImageMatrix
                return true
            }
        })

        mGestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                mImageMatrix!!.postScale(1.0f, 1.0f, mCenterX.toFloat(), mCenterY.toFloat())
                imageMatrix = mImageMatrix
                return true
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                //mImageMatrix.setTranslate(0, 0);
                //setImageMatrix(mImageMatrix);
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
        })

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            val cx = (w - drawable.intrinsicWidth) / 2
            val cy = (h - drawable.intrinsicHeight) / 2
            mImageMatrix!!.setTranslate(cx.toFloat(), cy.toFloat())
            imageMatrix = mImageMatrix

            mCenterX = w / 2
            mCenterY = h / 2
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        var retValue = mScaleGesture!!.onTouchEvent(event)

        retValue = mGestureDetector!!.onTouchEvent(event) || retValue

        return retValue || super.onTouchEvent(event)
    }

    companion object {

        private val TAG = "GestureImageView"

        // 最大缩放比例
        private val MAX_SCALE_FACTOR = 3.0f
        // 最小缩放比例
        private val MIN_SCALE_FACTOR = 0.3f
    }

}
