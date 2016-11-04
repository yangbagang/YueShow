package com.ybg.yxym.yueshow.http.listener

import com.ybg.yxym.yueshow.http.Model.Progress

interface ProgressListener {
    fun onProgress(progress: Progress)
}