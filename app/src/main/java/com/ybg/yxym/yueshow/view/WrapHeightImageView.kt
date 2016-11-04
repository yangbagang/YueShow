package com.ybg.yxym.yueshow.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * 宽度设置好了之后，自动根据src计算高度，让宽高比与src图片的宽高比一样
 */
class WrapHeightImageView : ImageView {

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
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
        val childWidthSize = measuredWidth
        var childHeightSize = measuredHeight
        val d = drawable
        var dw = 0
        var dh = 0
        if (d != null && d.intrinsicWidth > 0 && d.intrinsicHeight > 0) {
            childHeightSize = Math.round(1.0f * childWidthSize / d.intrinsicWidth * d.intrinsicHeight)
            dw = d.intrinsicWidth
            dh = d.intrinsicHeight
        } else if (defW > 0 && defH > 0) {
            childHeightSize = Math.round(1.0f * childWidthSize / defW * defH)
        }
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY)
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, View.MeasureSpec.EXACTLY)

        //        Log.d("test", "onMesure: "+defW+", "+defH+", "+dw+", "+dh+", "+childWidthSize+", "+childHeightSize+", "+widthMeasureSpec+", "+heightMeasureSpec+", "+d);

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
