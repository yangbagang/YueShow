package com.ybg.yxym.yueshow.utils

import android.os.AsyncTask

/**
 * Created by ybg on 17-2-21.
 */
object AsyncTaskUtil {

    fun startTask(task: () -> Unit, callback: () -> Unit) {
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                task()
            }

            override fun onPostExecute(result: Unit?) {
                callback()
            }
        }.execute()
    }
}