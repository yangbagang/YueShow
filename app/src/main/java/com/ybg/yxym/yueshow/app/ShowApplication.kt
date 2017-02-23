package com.ybg.yxym.yueshow.app

import android.content.Context
import com.igexin.sdk.PushManager
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.pgyersdk.crash.PgyCrashManager
import com.qiniu.pili.droid.streaming.StreamingEnv
import com.ybg.yxym.yb.app.YbgAPP
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.picasso.OkHttp3Downloader
import com.ybg.yxym.yueshow.picasso.Picasso
import java.io.File


/**
 * Created by yangbagang on 2016/10/27.
 */

class ShowApplication : YbgAPP() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        initImageLoader(applicationContext)
        StreamingEnv.init(applicationContext)

        //初始化picasso
        val picasso = Picasso.Builder(applicationContext).downloader(OkHttp3Downloader(File(AppConstants.IMAGE_CACHE_PATH))).build()
        Picasso.setSingletonInstance(picasso)

        PushManager.getInstance().initialize(applicationContext)
        PgyCrashManager.register(this)
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
