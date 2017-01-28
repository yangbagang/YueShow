package com.ybg.yxym.yueshow.view.live

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.ybg.yxym.yueshow.R

class HeartLayout : RelativeLayout {

    private var mAnimator: AbstractPathAnimator? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0)

        mAnimator = PathAnimator(AbstractPathAnimator.Config.fromTypeArray(a))

        a.recycle()
    }

    var animator: AbstractPathAnimator?
        get() = mAnimator
        set(animator) {
            clearAnimation()
            mAnimator = animator
        }

    override fun clearAnimation() {
        for (i in 0..childCount - 1) {
            getChildAt(i).clearAnimation()
        }
        removeAllViews()
    }

    fun addHeart(color: Int) {
        val heartView = HeartView(context)
        heartView.setColor(color)
        mAnimator!!.start(heartView, this)
    }

    fun addHeart(color: Int, heartResId: Int, heartBorderResId: Int) {
        val heartView = HeartView(context)
        heartView.setColorAndDrawables(color, heartResId, heartBorderResId)
        mAnimator!!.start(heartView, this)
    }

}
