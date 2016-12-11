package com.ybg.yxym.yueshow.app

import android.content.Context
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.ybg.yxym.yb.app.YbgAPP


/**
 * Created by yangbagang on 2016/10/27.
 */

class ShowApplication : YbgAPP() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        initImageLoader(applicationContext);
    }

    fun initImageLoader(context: Context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        val config = ImageLoaderConfiguration.Builder(context)
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        config.denyCacheImageMultipleSizesInMemory()
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(50 * 1024 * 1024) // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
        config.writeDebugLogs() // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build())
    }

    companion object {

        var instance: ShowApplication? = null
            private set
    }

    fun checkNeedLogin(message: String) = message.contains("重新登录")

    fun setAutoPlay(boolean: Boolean) {
        preference.setBoolean("autoPlay", boolean)
    }

    fun isAutoPlay(): Boolean {
        return preference.getBoolean("autoPlay", false)
    }

    fun setReceiverMsg(boolean: Boolean) {
        preference.setBoolean("receiverMsg", boolean)
    }

    fun isReceiverMsg(): Boolean {
        return preference.getBoolean("receiverMsg", false)
    }
}
