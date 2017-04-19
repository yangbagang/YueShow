package com.ybg.yxym.im.app;

import android.app.Application;

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
    }

    public static IMApplication getInstance() {
        return instance;
    }

}
