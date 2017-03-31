package com.ybg.yxym.yueshow.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.ybg.yxym.yueshow.R

/**
 * Created by ybg on 17-3-27.
 */
class BannerFrameItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private lateinit var img: ImageView
    private lateinit var txt: TextView

    init {
        initUI(context)
    }

    private fun initUI(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.banner_item, this, true)
        img = findViewById(R.id.bannerImage) as ImageView
        txt = findViewById(R.id.bannerText) as TextView
    }

    fun getBannerImg(): ImageView {
        return img
    }

    fun setImageBitmap(bitmap: Bitmap) {
        img.setImageBitmap(bitmap)
    }

    fun setProgress(progress: Int) {
        txt.text = "正在加载中("+progress+")"
        if (progress == 100) {
            txt.visibility = View.GONE
        }
    }

    fun setMsg(msg: String) {
        txt.text = msg
    }
}