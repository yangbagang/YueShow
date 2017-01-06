package com.ybg.yxym.yueshow.activity.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.VideoRecorderView.OnRecordFinishListener
import kotlinx.android.synthetic.main.activity_video_shot.*

class VideoShotActivity : BaseActivity() {

    private var isFinish = true
    private var isSuccess = false

    override fun setContentViewId(): Int {
        return R.layout.activity_video_shot
    }

    override fun setUpView() {
        setCustomTitle("视频录制")
    }

    override fun init() {
        shotBtn.setOnTouchListener { view, event ->
            if (event.action === MotionEvent.ACTION_DOWN) {//用户按下拍摄按钮
                //shotBtn.setBackgroundResource(R.drawable.bg_movie_add_shoot_select)
                videoRecorderView.record(object : OnRecordFinishListener {

                    override fun onRecordFinish() {
                        if (!isSuccess && videoRecorderView.timeCount < 10) {//判断用户按下时间是否大于10秒
                            isSuccess = true
                            handler.sendEmptyMessage(1)
                        }
                    }
                })
            } else if (event.getAction() === MotionEvent.ACTION_UP) {//用户抬起拍摄按钮
                //shotBtn.setBackgroundResource(R.drawable.bg_movie_add_shoot)
                if (videoRecorderView.timeCount > 3) {//判断用户按下时间是否大于3秒
                    if (!isSuccess) {
                        isSuccess = true
                        handler.sendEmptyMessage(1)
                    }
                } else {
                    isSuccess = false
                    if (videoRecorderView.getVecordFile() != null)
                        videoRecorderView.getVecordFile()?.delete()//删除录制的过短视频
                    videoRecorderView.stop()//停止录制
                    ToastUtil.show("视频录制时间太短")
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        isFinish = true;
        if (videoRecorderView.getVecordFile() != null)
            videoRecorderView.getVecordFile()?.delete()//视频使用后删除
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        isFinish = false
        isSuccess = false
        videoRecorderView.stop()//停止录制
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (isSuccess) {
                val videoFile = videoRecorderView.getVecordFile()?.absolutePath
                println("videoFile=$videoFile")
                VideoProcessActivity.start(mContext!!, videoFile ?: "")
                finish()
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, VideoShotActivity::class.java)
            context.startActivity(starter)
        }
    }
}
