package com.ybg.yxym.yb.app

import android.app.Activity
import android.app.Application
import com.ybg.yxym.yb.utils.AppConstat.PREFERENCE_FILE_NAME
import com.ybg.yxym.yb.utils.AppPreferences

/**
 * Created by yangbagang on 16/8/3.
 */
open class YbgAPP : Application() {

    protected val preference: AppPreferences = AppPreferences.instance

    var longitude = 0.0

    var latitude = 0.0

    fun hasLogin(): Boolean {
        return "" != token
    }

    var token: String
        get() = preference.getString("token", "")
        set(token) = preference.setString("token", token)

    override fun onCreate() {
        super.onCreate()

        if (!preference.hasInit()) {
            preference.init(getSharedPreferences(
                    PREFERENCE_FILE_NAME, Activity.MODE_PRIVATE))
        }
    }
}
