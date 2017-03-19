package com.ybg.yxym.yueshow.activity.show

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import com.pili.pldroid.player.AVOptions
import com.pili.pldroid.player.PLMediaPlayer
import com.pili.pldroid.player.widget.PLVideoTextureView
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.view.MediaController
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : Activity() {

    //视频播放相关
    private val mIsLiveStreaming = 0
    private var videoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_video_player)

        initVideoView()

        if (intent != null) {
            val url = intent.getStringExtra("videoUrl")
            if (url != null && url != "") {
                videoUrl = url
                v_player.setVideoPath(videoUrl)
                v_player.start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (videoUrl != null) {
            v_player.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoUrl != null) {
            v_player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (videoUrl != null) {
            v_player.stopPlayback()
        }
    }

    private fun initVideoView() {
        v_player.setCoverView(iv_video_cover)
        v_player.setBufferingIndicator(loadingView)
        loadingView.visibility = View.VISIBLE

        setOptions(0)

        val mMediaController = MediaController(this)
        v_player.setMediaController(mMediaController)
        v_player.displayAspectRatio = PLVideoTextureView.ASPECT_RATIO_FIT_PARENT
        v_player.setOnPreparedListener(VideoPreparedListener())
        v_player.setOnCompletionListener(VideoCompletionListener())
        v_player.setOnVideoSizeChangedListener(VideoSizeChangedListener())

        iv_video_cover.setOnClickListener {
            if (videoUrl != null) {
                v_player.start()
            }
        }
    }

    private inner class VideoPreparedListener : PLMediaPlayer.OnPreparedListener {

        override fun onPrepared(mediaPlayer: PLMediaPlayer?) {
            //动画放大
            val animationSet = AnimationSet(true)
            val alphaAnimation = AlphaAnimation(1f, 0f)
            alphaAnimation.duration = 1000
            animationSet.addAnimation(alphaAnimation)
            val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f)
            scaleAnimation.duration = 1000
            animationSet.addAnimation(scaleAnimation)
            v_player.startAnimation(animationSet)
        }

    }

    private inner class VideoCompletionListener : PLMediaPlayer.OnCompletionListener {

        override fun onCompletion(mediaPlayer: PLMediaPlayer?) {
            //动画缩小
            val animationSet = AnimationSet(true)
            val alphaAnimation = AlphaAnimation(0f, 1f)
            alphaAnimation.duration = 1000
            animationSet.addAnimation(alphaAnimation)
            val scaleAnimation = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            scaleAnimation.duration = 1000
            animationSet.addAnimation(scaleAnimation)
            v_player.startAnimation(animationSet)
            //结束退出
            finish()
        }

    }

    private inner class VideoSizeChangedListener : PLMediaPlayer.OnVideoSizeChangedListener {

        override fun onVideoSizeChanged(mediaPlayer: PLMediaPlayer?, width: Int, height: Int) {
            if ((v_player.width < v_player.height && rl_video.width > rl_video.height) ||
                    (v_player.width > v_player.height && rl_video.width < rl_video.height)) {
                v_player.displayOrientation = 270
            }
        }

    }

    private fun setOptions(codecType: Int) {
        val options = AVOptions()

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000)
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024)
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming)
        if (mIsLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1)
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType)

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0)

        v_player.setAVOptions(options)
    }

    companion object {

        fun start(context: Context, videoUrl: String) {
            val starter = Intent(context, VideoPlayerActivity::class.java)
            starter.putExtra("videoUrl", videoUrl)
            context.startActivity(starter)
        }
    }
}
