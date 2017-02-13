package com.ybg.yxym.yueshow.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.transformer.ZoomOutPageTransformer
import com.ybg.yxym.yueshow.utils.ScreenUtils
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Created by yangbagang on 16/8/18.
 */
class BannerFrame @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                            defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
    private var imageViewList: MutableList<ImageView> = ArrayList<ImageView>()
    private lateinit var viewIndex: TextView
    private lateinit var mViewPager: ViewPager
    private var currentItem = 0
    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var w = 0
    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mViewPager.currentItem = currentItem
        }
    }

    init {
        initUI(context)
    }

    private fun initUI(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.banner_layout, this, true)
        viewIndex = findViewById(R.id.view_index) as TextView
        mViewPager = findViewById(R.id.view_page) as ViewPager
        mViewPager.setPageTransformer(true, ZoomOutPageTransformer())
        w = ScreenUtils.getScreenWidth(context)
    }

    fun setImageResources(imageUrls: List<String>) {
        imageViewList.clear()
        for (url in imageUrls) {
            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER//铺满屏幕

            ImageLoader.getInstance().displayImage(HttpUrl.getImageUrl(url), imageView, object :
                    ImageLoadingListener{
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    val p = imageView.layoutParams
                    if (p != null) {
                        p.width = loadedImage?.width ?: w
                        p.height = loadedImage?.height ?: (w * 0.75).toInt()
                        imageView.layoutParams = p
                    }
                }

                override fun onLoadingCancelled(imageUri: String?, view: View?) {

                }

                override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {

                }

                override fun onLoadingStarted(imageUri: String?, view: View?) {

                }

            })
            imageViewList.add(imageView)
        }
        mViewPager.isFocusable = true
        mViewPager.adapter = MyPagerAdapter()
        mViewPager.setOnPageChangeListener(MyPageChangeListener())
        viewIndex.text = "${imageViewList.size} - 1"
    }


    fun startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        scheduledExecutorService!!.scheduleAtFixedRate(BannerTask(), 1, 4, TimeUnit.SECONDS)
    }

    fun stopPlay() {
        scheduledExecutorService!!.shutdown()
    }

    private inner class MyPagerAdapter : PagerAdapter() {
        override fun destroyItem(container: View?, position: Int, `object`: Any?) {
            (container as ViewPager).removeView(imageViewList[position])
        }

        override fun instantiateItem(container: View?, position: Int): Any {
            (container as ViewPager).addView(imageViewList[position])
            return imageViewList[position]
        }

        override fun getCount(): Int {
            return imageViewList.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun restoreState(arg0: Parcelable?, arg1: ClassLoader?) {
        }

        override fun saveState(): Parcelable? {
            return null
        }
    }


    private inner class MyPageChangeListener : ViewPager.OnPageChangeListener {
        internal var isAutoPlay = false

        override fun onPageScrollStateChanged(arg0: Int) {
            when (arg0) {
                1 -> isAutoPlay = false
                2 -> isAutoPlay = true
                0 -> if (mViewPager.currentItem == mViewPager.adapter.count - 1 && !isAutoPlay) {
                    mViewPager.currentItem = 0
                } else if (mViewPager.currentItem == 0 && !isAutoPlay) {
                    mViewPager.currentItem = mViewPager.adapter.count - 1
                }//如果滑到头就从尾开始
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        }

        override fun onPageSelected(pos: Int) {
            viewIndex.text = "${imageViewList.size} - ${pos + 1}"
        }
    }


    private inner class BannerTask : Runnable {
        override fun run() {
            synchronized(mViewPager) {
                currentItem = (currentItem + 1) % imageViewList.size
                handler.obtainMessage().sendToTarget()
            }
        }
    }

    companion object {

        private val isAutoPlay = true
    }
}
