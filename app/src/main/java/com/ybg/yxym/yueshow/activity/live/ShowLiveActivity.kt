package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.pili.pldroid.player.AVOptions
import com.pili.pldroid.player.widget.PLVideoView
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.view.MediaController

class ShowLiveActivity : BaseActivity() {

    private var show: YueShow? = null
    private var url: String? = null
    private lateinit var mVideoView: PLVideoView
    private lateinit var mCoverView: ImageView

    private val mIsLiveStreaming = 1

    override fun setContentViewId(): Int {
        return R.layout.activity_show_live
    }

    override fun setUpView() {
        mVideoView = findViewById(R.id.PLVideoView) as PLVideoView
        mCoverView = findViewById(R.id.CoverView) as ImageView

        mVideoView.setCoverView(mCoverView)
        val mLoadingView = findViewById(R.id.LoadingView)
        mVideoView.setBufferingIndicator(mLoadingView)
        mLoadingView.visibility = View.VISIBLE

        setOptions(0)

        val mMediaController = MediaController(this)
        mVideoView.setMediaController(mMediaController)
        mVideoView.displayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT
        mVideoView.setVideoPath(url)
    }

    override fun init() {
        if (intent != null) {
            show = intent.extras.getSerializable("show") as YueShow
            url = intent.extras.getString("url")
        }
    }

    override fun onResume() {
        super.onResume()
        mVideoView.start()
    }

    override fun onPause() {
        super.onPause()
        mVideoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.stopPlayback()
    }

    private fun setOptions(codecType: Int) {
        val options = AVOptions()

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000)
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024)
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming)
        if (mIsLiveStreaming === 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1)
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType)

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0)

        mVideoView.setAVOptions(options)
    }

    companion object {
        fun start(context: Context, show: YueShow, url: String) {
            val starter = Intent(context, ShowLiveActivity::class.java)
            starter.putExtra("show", show)
            starter.putExtra("url", url)
            context.startActivity(starter)
        }
    }
}
