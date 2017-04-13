package com.ybg.yxym.yueshow.openIM

import android.app.Application
import com.alibaba.mobileim.YWAPI
import com.alibaba.wxlib.util.SysUtil
import com.squareup.leakcanary.LeakCanary
import com.ybg.yxym.yueshow.app.ShowApplication

/**
 * Created by yangbagang on 2017/4/13.
 */
object InitHelper {

    fun initYWSDK(application: ShowApplication) {
        //只在主进程进行云旺SDK的初始化!!!
        if (SysUtil.isMainProcess()) {
            //注意：--------------------------------------
            //以下步骤调用顺序有严格要求，请按照示例的步骤
            //的顺序调用！

            // ------[step1]-------------
            //［IM定制初始化］，如果不需要定制化，可以去掉此方法的调用
            //注意：由于增加全局初始化，该配置需最先执行！

            CustomInitHelper.initCustom()

            // ------[step2]-------------
            //SDK初始化
            LoginHelper.instance.initSDK(application)

            //后期将使用Override的方式进行集中配置，请参照YWSDKGlobalConfigSample
            YWAPI.enableSDKLogOutput(true)
            LeakCanary.install(application)
        }
    }

}