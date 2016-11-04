package com.ybg.yxym.yueshow.app

import com.ybg.yxym.yb.app.YbgAPP

/**
 * Created by yangbagang on 2016/10/27.
 */

class ShowApplication : YbgAPP() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        var instance: ShowApplication? = null
            private set
    }
}
