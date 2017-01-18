package com.ybg.yxym.yueshow.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Environment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.LinearLayout
import android.widget.ProgressBar

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.constant.AppConstants

import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

/**
 * Created by yangbagang on 2017/1/6.
 */

class VideoRecorderView
@SuppressLint("NewApi")
constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : LinearLayout(context, attrs,
        defStyle), MediaRecorder.OnErrorListener {

    private val mSurfaceView: SurfaceView
    private val mSurfaceHolder: SurfaceHolder
    private val mProgressBar: ProgressBar

    private var mMediaRecorder: MediaRecorder? = null
    private var mCamera: Camera? = null
    private var mTimer: Timer? = null// 计时器
    private var mOnRecordFinishListener: OnRecordFinishListener? = null// 录制完成回调接口

    private val mWidth: Int// 视频分辨率宽度
    private val mHeight: Int// 视频分辨率高度
    private val isOpenCamera: Boolean// 是否一开始就打开摄像头
    private val mRecordMaxTime: Int// 一次拍摄最长时间
    var timeCount: Int = 0// 时间计数
    private var mVecordFile: File? = null// 文件

    private var cameraFlag = 0

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0) {}

    init {

        val a = context.obtainStyledAttributes(attrs,
                R.styleable.VideoRecorderView, defStyle, 0)
        mWidth = a.getInteger(R.styleable.VideoRecorderView_width, 320)// 默认320
        mHeight = a.getInteger(R.styleable.VideoRecorderView_height, 240)// 默认240

        isOpenCamera = a.getBoolean(
                R.styleable.VideoRecorderView_is_open_camera, true)// 默认打开
        mRecordMaxTime = a.getInteger(
                R.styleable.VideoRecorderView_record_max_time, 10)// 默认为10

        LayoutInflater.from(context)
                .inflate(R.layout.video_recorder_view, this)
        mSurfaceView = findViewById(R.id.surfaceView) as SurfaceView
        mProgressBar = findViewById(R.id.progressBar) as ProgressBar
        mProgressBar.max = mRecordMaxTime// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.holder
        mSurfaceHolder.addCallback(CustomCallBack())
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        a.recycle()
    }

    /**

     */
    private inner class CustomCallBack : android.view.SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            if (!isOpenCamera)
                return
            try {
                initCamera()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                    height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            if (!isOpenCamera)
                return
            freeCameraResource()
        }

    }

    /**
     * 初始化摄像头
     */
    @Throws(IOException::class)
    private fun initCamera() {
        if (mCamera != null) {
            freeCameraResource()
        }
        try {
            if (cameraFlag == 0) {
                //后置摄像头
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
            } else {
                //前置摄像头
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            freeCameraResource()
        }

        if (mCamera == null)
            return

        setCameraParams()
        mCamera!!.setDisplayOrientation(90)
        mCamera!!.setPreviewDisplay(mSurfaceHolder)
        mCamera!!.startPreview()
        mCamera!!.unlock()
    }

    /**
     * 设置摄像头为竖屏
     */
    private fun setCameraParams() {
        if (mCamera != null) {
            val params = mCamera!!.parameters
            params.set("orientation", "portrait")
            mCamera!!.parameters = params
        }
    }

    fun setFlashMode(flashMode: Int) {
        println("设置闪光灯模式")
        try {
            if (mCamera != null) {
                mCamera?.lock()
                val params = mCamera!!.parameters
                if (flashMode == 0) {
                    params.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    params.set("flash-mode", "off")
                } else if (flashMode == 1) {
                    params.flashMode = Camera.Parameters.FLASH_MODE_ON
                    params.set("flash-mode", "on")
                } else if (flashMode == 2) {
                    params.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                    params.set("flash-mode", "auto")
                }
                mCamera!!.parameters = params
                mCamera?.unlock()
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun switchCamera() {
        if (cameraFlag == 0) {
            cameraFlag = 1
            freeCameraResource()
            initCamera()
        } else {
            cameraFlag = 0
            freeCameraResource()
            initCamera()
        }
    }

    /**
     * 释放摄像头资源
     */
    private fun freeCameraResource() {
        if (mCamera != null) {
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            mCamera!!.lock()
            mCamera!!.release()
            mCamera = null
        }
    }

    private fun createRecordDir() {
        //录制的视频保存文件夹
        val sampleDir = File(AppConstants.IMAGE_SAVE_PATH + "/video/")//录制视频的保存地址
        if (!sampleDir.exists()) {
            sampleDir.mkdirs()
        }
        val vecordDir = sampleDir
        // 创建文件
        try {
            //mp4格式的录制的视频文件
            mVecordFile = File.createTempFile("" + System.currentTimeMillis(), ".mp4", vecordDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 初始化
     * @throws IOException
     */
    @SuppressLint("NewApi")
    @Throws(IOException::class)
    private fun initRecord() {
        mMediaRecorder = MediaRecorder()
        mMediaRecorder!!.reset()
        if (mCamera != null)
            mMediaRecorder!!.setCamera(mCamera)
        mMediaRecorder!!.setOnErrorListener(this)
        mMediaRecorder!!.setPreviewDisplay(mSurfaceHolder.surface)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)// 视频源
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)// 音频源
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)// 视频输出格式
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)// 音频格式
        mMediaRecorder!!.setVideoSize(mWidth, mHeight)// 设置分辨率：
        // mMediaRecorder.setVideoFrameRate(16);// 这个我把它去掉了，感觉没什么用
        mMediaRecorder!!.setVideoEncodingBitRate(1 * 1024 * 1024 * 100)// 设置帧频率，然后就清晰了
        mMediaRecorder!!.setOrientationHint(90)// 输出旋转90度，保持竖屏录制
        mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP)// 视频录制格式
        // mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
        mMediaRecorder!!.setOutputFile(mVecordFile!!.absolutePath)
        mMediaRecorder!!.prepare()
        try {
            mMediaRecorder!!.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 开始录制视频
     * @param onRecordFinishListener
     * *            达到指定时间之后回调接口
     */
    fun record(onRecordFinishListener: OnRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener
        createRecordDir()
        try {
            if (!isOpenCamera)
            // 如果未打开摄像头，则打开
                initCamera()
            initRecord()
            timeCount = 0// 时间计数器重新赋值
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {

                override fun run() {
                    timeCount++
                    mProgressBar.progress = timeCount// 设置进度条
                    if (timeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop()
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener!!.onRecordFinish()
                    }
                }
            }, 0, 1000)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 停止拍摄
     */
    fun stop() {
        stopRecord()
        releaseRecord()
        freeCameraResource()
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        mProgressBar.progress = 0
        if (mTimer != null)
            mTimer!!.cancel()
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder!!.setOnErrorListener(null)
            mMediaRecorder!!.setPreviewDisplay(null)
            try {
                mMediaRecorder!!.stop()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 释放资源
     */
    private fun releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.setOnErrorListener(null)
            try {
                mMediaRecorder!!.release()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        mMediaRecorder = null
    }

    //返回录制的视频文件
    fun getVecordFile(): File? {
        return mVecordFile
    }

    /**
     * 录制完成回调接口
     */
    interface OnRecordFinishListener {
        fun onRecordFinish()
    }

    override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
        try {
            mr?.reset()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
