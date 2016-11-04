package com.ybg.yxym.yueshow.http.handler

import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.listener.UIProgressListener

class UIHandler(uiProgressListener: UIProgressListener) : ProgressHandler(uiProgressListener) {

    override fun start(listener: UIProgressListener) {
        listener.onUIStart()
    }

    override fun progress(uiProgressListener: UIProgressListener, progress: Progress) {
        uiProgressListener.onUIProgress(progress)
    }

    override fun finish(listener: UIProgressListener) {
        listener.onUIFinish()
    }
}
