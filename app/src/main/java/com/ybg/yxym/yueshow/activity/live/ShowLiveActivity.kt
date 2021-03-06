package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.google.gson.jpush.reflect.TypeToken
import com.pili.pldroid.player.AVOptions
import com.pili.pldroid.player.PLMediaPlayer
import com.pili.pldroid.player.widget.PLVideoView
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.gift.GiftListActivity
import com.ybg.yxym.yueshow.activity.gift.SendGiftActivity
import com.ybg.yxym.yueshow.constant.MessageEvent
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.MediaController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.Serializable

class ShowLiveActivity : LivingBaseActivity() {

    private var show: YueShow? = null
    private var url: String? = null
    private lateinit var mVideoView: PLVideoView
    private lateinit var mCoverView: ImageView

    private val mIsLiveStreaming = 1

    private var author: UserBase? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_show_live
    }

    override fun setUpView() {
        instance = this
        //setCustomTitle("观看直播")

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

        initLiveBase()
    }

    override fun init() {
        if (intent != null) {
            show = intent.extras.getSerializable("show") as YueShow
            if (show != null) {
                loadAuthorInfo()
            }
            url = intent.extras.getString("url")
            val users = intent.extras.getSerializable("userList") as List<UserBase>
            mVideoView.setVideoPath(url)
            mVideoView.start()
            userList.addAll(users)
            userAvatarAdapter.setDataList(userList)
            userAvatarAdapter.notifyDataSetChanged()

            mVideoView.setOnCompletionListener(VideoCompleteListener())
        }

        EventBus.getDefault().register(this)
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

        leaveLiveShow()
        instance = null

        EventBus.getDefault().unregister(this)
    }

    private fun loadAuthorInfo() {
        if (show == null) {
            return
        }
        SendRequest.getAuthorInfo(mContext!!, show!!.id!!, mApplication.token, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val user = mGson!!.fromJson<UserBase>(jsonBean.data, object : TypeToken<UserBase>(){}.type)
                    author = user
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    @Subscribe
    fun onEvent(event: MessageEvent) {//处理EventBus 发送的消息的方法,具体操作在子类实现
        if (event.what == MessageEvent.MESSAGE_SEND_GIFT) {
            val msg = event.obj
            if (msg != null && msg is String) {
                SendRequest.sendLiveMsg(mContext!!, mApplication.token, "${show?.id}", "0", "1", msg,
                        object : OkCallback<String>(OkStringParser()){
                            override fun onSuccess(code: Int, response: String) {
                                runOnUiThread {
                                    msgText.setText("")
                                    liveToolBar.visibility = View.VISIBLE
                                    liveMsgTool.visibility = View.GONE
                                }
                            }

                            override fun onFailure(e: Throwable) {

                            }
                        })
            }
        }
    }

    override fun sendLiveMsg(msg: String, flag: Int, call: () -> Unit) {
        SendRequest.sendLiveMsg(mContext!!, mApplication.token, "${show?.id}", "$flag", "2", msg,
                object : OkCallback<String>(OkStringParser()) {
                    override fun onSuccess(code: Int, response: String) {
                        call()
                    }

                    override fun onFailure(e: Throwable) {
                        ToastUtil.show("发送失败，请稍候再试。")
                    }
                })
    }

    override fun openGiftWin() {
        if (author != null) {
            GiftListActivity.start(mContext!!, author!!.id, author!!.ymCode, 0)
        }
    }

    private fun setOptions(codecType: Int) {
        val options = AVOptions()

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 60 * 1000)
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 60 * 1000)
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

    private fun leaveLiveShow() {
        if (show == null) {
            return
        }
        SendRequest.leaveLive(mContext!!, mApplication.token, "${show!!.id}", object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                //nothing
            }

            override fun onFailure(e: Throwable) {
                //nothing
            }
        })
    }

    private inner class VideoCompleteListener : PLMediaPlayer.OnCompletionListener {

        override fun onCompletion(p0: PLMediaPlayer?) {
            SendRequest.checkLiveStatus(mContext!!, show!!.id!!, object : OkCallback<String>
            (OkStringParser()) {
                override fun onSuccess(code: Int, response: String) {
                    val jsonBean = JSonResultBean.fromJSON(response)
                    if (jsonBean != null && jsonBean.isSuccess) {
                        if (jsonBean.data == "1") {
                            //reconnect
                            mVideoView.setVideoPath(url)
                            mVideoView.start()
                        } else {
                            //转向结束界面
                            EndingLiveActivity.start(mContext!!, show!!.id!!)
                            finish()
                        }
                    }
                }

                override fun onFailure(e: Throwable) {

                }
            })
        }

    }

    companion object {

        var instance: ShowLiveActivity? = null

        fun start(context: Context, show: YueShow, userList: List<UserBase>, url: String) {
            val starter = Intent(context, ShowLiveActivity::class.java)
            starter.putExtra("show", show)
            starter.putExtra("url", url)
            starter.putExtra("userList", userList as Serializable)
            context.startActivity(starter)
        }

    }
}
