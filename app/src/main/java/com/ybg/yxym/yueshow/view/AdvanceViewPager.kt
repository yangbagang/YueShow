package com.ybg.yxym.yueshow.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by yangbagang on 2016/10/26.
 */

class AdvanceViewPager : ViewPager {

    private var mCanScroll = false

    fun setViewCanScroll(b: Boolean) {
        mCanScroll = b
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }


    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!mCanScroll) {
            return false
        } else {
            return super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!mCanScroll) {
            return false
        } else {
            return super.onInterceptTouchEvent(ev)
        }
    }

}
