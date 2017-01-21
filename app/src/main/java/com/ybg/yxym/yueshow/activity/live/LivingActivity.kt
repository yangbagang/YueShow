package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.gson.reflect.TypeToken
import com.qiniu.pili.droid.streaming.*
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_living.*
import org.json.JSONException
import org.json.JSONObject

class LivingActivity : BaseActivity(), StreamingStateChangedListener {

    private var show: YueShow? = null
    private var url: String? = null

    private var time = 5//倒计时

    private var mMediaStreamingManager: MediaStreamingManager? = null
    private var mProfile: StreamingProfile? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_living
    }

    override fun setUpView() {
        setCustomTitle("正在直播")
    }

    override fun onResume() {
        super.onResume()
        mMediaStreamingManager?.resume()
    }

    override fun onPause() {
        super.onPause()
        // You must invoke pause here.
        mMediaStreamingManager?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timeHandler != null) {
            timeHandler?.removeCallbacks(null)
            timeHandler = null
        }
        time = 0
    }

    override fun onStateChanged(streamingState: StreamingState, extra: Any) {
        when (streamingState) {
            StreamingState.PREPARING -> {
            }
            StreamingState.READY -> {
                if (time == 0) {
                    // start streaming when READY
                    Thread(Runnable {
                        if (mMediaStreamingManager != null) {
                            mMediaStreamingManager?.startStreaming()
                        }
                    }).start()
                }
            }
            StreamingState.CONNECTING -> {
            }
            StreamingState.STREAMING -> {
            }
            StreamingState.SHUTDOWN -> {
            }
            StreamingState.IOERROR -> {
            }
            StreamingState.OPEN_CAMERA_FAIL -> {
            }
            StreamingState.DISCONNECTED -> {
            }
        }
    }

    override fun init() {
        if (intent != null) {
            show = intent.extras.getSerializable("show") as YueShow
            url = intent.extras.getString("url")
        }
        val afl = findViewById(R.id.cameraPreview_afl) as AspectFrameLayout

        // Decide FULL screen or real size
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL)
        val glSurfaceView = findViewById(R.id.cameraPreview_surfaceView) as GLSurfaceView

        try {
            val mJSONObject = JSONObject(url)
            val stream = StreamingProfile.Stream(mJSONObject)
            mProfile = StreamingProfile()
            mProfile!!.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY).stream = stream
            // You can invoke this before startStreaming, but not in initialization phase.

            val setting = CameraStreamingSetting()
            setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                    .setContinuousFocusModeEnabled(true)
                    .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                    .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)

            mMediaStreamingManager = MediaStreamingManager(this, afl, glSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC)
            // soft codec

            mMediaStreamingManager?.prepare(setting, mProfile)
            mMediaStreamingManager?.setStreamingStateListener(this@LivingActivity)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        timeHandler?.postDelayed(runnable, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.complete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_finish) {
            closeLive()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun closeLive() {
        SendRequest.closeLive(mContext!!, mApplication.token, "${show?.id}", object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val json = JSONObject(jsonBean.data)
                    val num = json.getInt("num")
                    val show = mGson!!.fromJson<YueShow>(json.getString("show"), object
                        : TypeToken<YueShow>(){}.type)
                    EndingLiveActivity.start(mContext!!, show, num)
                    finish()
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                e.printStackTrace()
                ToastUtil.show("关闭失败")
            }
        })
    }

    private val runnable = object : Runnable {
        override fun run() {
            time--
            if (time == 0) {
                // 关闭倒计时
                timeCover.visibility = View.GONE
                // start streaming when READY
                Thread(Runnable {
                    if (mMediaStreamingManager != null) {
                        mMediaStreamingManager?.startStreaming()
                    }
                }).start()
            } else {
                timeHandler?.sendEmptyMessage(158)
            }
            timeHandler?.postDelayed(this, 1000)
        }
    }

    private var timeHandler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                158 -> tv_time.text = "$time"
            }
            super.handleMessage(msg)
        }
    }

    companion object {
        fun start(context: Context, show: YueShow, url: String) {
            val starter = Intent(context, LivingActivity::class.java)
            starter.putExtra("show", show)
            starter.putExtra("url", url)
            context.startActivity(starter)
        }
    }

}