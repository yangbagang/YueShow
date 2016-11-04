package com.ybg.yxym.yueshow.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * 高度设置好了之后，自动根据src计算宽度，让宽高比与src图片的宽高比一样

 */
class WrapWidthImageView : ImageView {

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context) : super(context) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view. We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec), View.getDefaultSize(0, heightMeasureSpec))

        // Children are just made to fill our space.
        var childWidthSize = measuredWidth
        val childHeightSize = measuredHeight
        val d = drawable
        if (d != null && d.intrinsicWidth > 0 && d.intrinsicHeight > 0) {
            childWidthSize = Math.round(1.0f * childHeightSize.toFloat() * d.intrinsicWidth.toFloat() / d.intrinsicHeight)
        } else if (defW > 0 && defH > 0) {
            childWidthSize = Math.round(1.0f * childHeightSize.toFloat() * defW.toFloat() / defH)
        }
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY)
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    internal var defW = 0
    internal var defH = 0
    /**
     * 设置默认的宽高比
     * @param w
     * *
     * @param h
     */
    fun setDefaultWH(w: Int, h: Int) {
        defW = w
        defH = h
    }
}
