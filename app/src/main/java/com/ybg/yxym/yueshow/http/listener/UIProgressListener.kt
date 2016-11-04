package com.ybg.yxym.yueshow.http.listener

import com.ybg.yxym.yueshow.http.Model.Progress

interface UIProgressListener {

    fun onUIProgress(progress: Progress)

    fun onUIStart()

    fun onUIFinish()
}
