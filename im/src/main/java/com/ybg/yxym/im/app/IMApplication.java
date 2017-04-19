package com.ybg.yxym.im.app;

import android.app.Application;

import com.ybg.yxym.im.chatting.utils.SharePreferenceManager;
import com.ybg.yxym.im.constants.IMConstants;
import com.ybg.yxym.im.receiver.NotificationClickEventReceiver;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by yangbagang on 2017/4/17.
 */
public class IMApplication extends Application {

    private static IMApplication instance = null;

    private boolean needAtMsg = true;

    public boolean isNeedAtMsg() {
        return needAtMsg;
    }

    public void setNeedAtMsg(boolean needAtMsg) {
        this.needAtMsg = needAtMsg;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //初始化JMessage-sdk，第二个参数表示开启漫游
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext(), true);
        SharePreferenceManager.init(getApplicationContext(), IMConstants.JCHAT_CONFIGS);
        //设置Notification的模式
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());
    }

    public static IMApplication getInstance() {
        return instance;
    }

}
