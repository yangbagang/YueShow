package com.ybg.yxym.yueshow.activity.photo

import android.annotation.TargetApi
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.constant.MessageHandler
import com.ybg.yxym.yueshow.utils.*
import com.ybg.yxym.yueshow.utils.camera.CameraHelper
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Method
import java.util.*

/**
 * 相机页面自定义拍照
 */
@SuppressWarnings("deprecation") //用到和部分过时的API
class CameraActivity : BaseActivity() {
    private var mCameraHelper: CameraHelper? = null
    private var parameters: Camera.Parameters? = null
    private var cameraInst: Camera? = null
    private var bundle: Bundle? = null
    private val photoWidth = ScreenUtils.cameraPhotoWidth
    private val photoNumber = 4
    private val photoMargin = ResourcesUtils.dip2px(1)
    private var pointX: Float = 0.toFloat()
    private var pointY: Float = 0.toFloat()
    private var mode: Int = 0                      //0是聚焦 1是放大
    private var dist: Float = 0.toFloat()
    private var PHOTO_SIZE = 2000//放大缩小
    internal var curZoomValue = 0
    private var mCurrentCameraId = 0  //1是前置 0是后置
    private var adapterSize: Camera.Size? = null
    private var previewSize: Camera.Size? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_camera
    }

    override fun setUpView() {
        /** 6.0 动态申请权限 外部存储和相机 */
        AndroidPermissonRequest.verifyCameraPermissions(mContext!!);
    }

    override fun init() {
        mCameraHelper = CameraHelper(mContext)
        //SurfaceView相关设置
        val surfaceHolder = sv_take_photo!!.holder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceHolder.setKeepScreenOn(true)
        surfaceHolder.addCallback(mSurfaceCallback)
        sv_take_photo!!.isFocusable = true
        sv_take_photo!!.setBackgroundColor(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
        canSwitchCamera()
        sv_take_photo!!.setOnTouchListener(mOnTouchListener)
        //设置相机界面,照片列表,以及拍照布局的高度(保证相机预览为正方形)
        var params = cg_camera_grid!!.getLayoutParams()
        params.height = ScreenUtils.getScreenWidth(mContext!!)
        params.height = ScreenUtils.cameraPhotoAreaHeight
        params = rl_take_parent!!.layoutParams
        params.height = ScreenUtils.getScreenHeight(mContext!!) - ScreenUtils.getScreenWidth(mContext!!) - ScreenUtils.getStatusHeight(mContext!!) - ScreenUtils.cameraPhotoAreaHeight
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_switch_camera//切换摄像头
            -> switchCamera()
            R.id.iv_camera_close//关闭页面/摄像头
            -> finish()
            R.id.iv_camera_flash//闪光灯
            -> turnLight(cameraInst)
            R.id.iv_take_photo//拍照
            -> try {
                cameraInst!!.takePicture(null, null, mPictureCallback)
            } catch (e: Exception) {
                ToastUtil.show("拍照失败，请重试!")
                cameraInst!!.startPreview()
            }

            R.id.sv_take_photo -> {
                try {
                    pointFocus(pointX.toInt(), pointY.toInt())
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val layout = RelativeLayout.LayoutParams(view_focus_index!!.layoutParams)
                layout.setMargins(pointX.toInt() - 60, pointY.toInt() - 60, 0, 0)
                view_focus_index!!.layoutParams = layout
                view_focus_index!!.visibility = View.VISIBLE
                val sa = ScaleAnimation(3f, 1f, 3f, 1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
                sa.duration = 800
                view_focus_index!!.startAnimation(sa)
                mHandler!!.sendEmptyMessageDelayed(MessageHandler.MESSAGE_CAMERA_FOCUS, 800)
            }
            R.id.rl_take_parent, R.id.rl_camera_top -> {
            }
        }//不用处理,防止聚焦的错乱

    }

    override fun onHandler(msg: Message) {
        super.onHandler(msg)
        when (msg.what) {
            MessageHandler.MESSAGE_CAMERA_FOCUS -> view_focus_index!!.visibility = View.INVISIBLE
        }
    }

    private val mOnTouchListener = View.OnTouchListener { v, event ->
        when (event.action and MotionEvent.ACTION_MASK) {
        // 主点按下
            MotionEvent.ACTION_DOWN -> {
                pointX = event.x
                pointY = event.y
                mode = FOCUS
            }
        // 副点按下
            MotionEvent.ACTION_POINTER_DOWN -> {
                dist = spacing(event)
                // 如果连续两点距离大于10，则判定为多点模式
                if (spacing(event) > 10f) {
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = FOCUS
            MotionEvent.ACTION_MOVE -> if (mode == FOCUS) {
                //pointFocus((int) event.getRawX(), (int) event.getRawY());
            } else if (mode == ZOOM) {
                val newDist = spacing(event)
                if (newDist > 10f) {
                    var tScale = (newDist - dist) / dist
                    if (tScale < 0) {
                        tScale = tScale * 10
                    }
                    addZoomIn(tScale.toInt())
                }
            }
        }
        false
    }

    /**
     * 两点的距离
     */
    private fun spacing(event: MotionEvent?): Float {
        if (event == null) {
            return 0f
        }
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun addZoomIn(delta: Int) {

        try {
            val params = cameraInst!!.parameters
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported)
            if (!params.isZoomSupported) {
                return
            }
            curZoomValue += delta
            if (curZoomValue < 0) {
                curZoomValue = 0
            } else if (curZoomValue > params.maxZoom) {
                curZoomValue = params.maxZoom
            }

            if (!params.isSmoothZoomSupported) {
                params.zoom = curZoomValue
                cameraInst!!.parameters = params
            } else {
                cameraInst!!.startSmoothZoom(curZoomValue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //切换前后置摄像头
    private fun switchCamera() {
        mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper!!.getNumberOfCameras()
        releaseCamera()
        Log.d("DDDD", "DDDD----mCurrentCameraId" + mCurrentCameraId)
        setUpCamera(mCurrentCameraId)
    }

    private fun releaseCamera() {
        if (cameraInst != null) {
            cameraInst!!.setPreviewCallback(null)
            cameraInst!!.release()
            cameraInst = null
        }
        adapterSize = null
        previewSize = null
    }

    /**
     * @param mCurrentCameraId2
     */
    private fun setUpCamera(mCurrentCameraId2: Int) {
        cameraInst = getCameraInstance(mCurrentCameraId2)
        if (cameraInst != null) {
            try {
                cameraInst!!.setPreviewDisplay(sv_take_photo!!.holder)
                initCamera()
                cameraInst!!.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            ToastUtil.show("切换失败，请重试！")
        }
    }

    private fun getCameraInstance(id: Int): Camera? {
        var c: Camera? = null
        try {
            c = mCameraHelper!!.openCamera(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return c
    }

    private fun canSwitchCamera() {
        try {
            val canSwitch = mCameraHelper!!.hasFrontCamera() && mCameraHelper!!.hasBackCamera()
            iv_switch_camera!!.visibility = if (canSwitch) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(TAG + "获取相机信息失败")
        }

    }

    /**
     * 闪光灯开关   开->关->自动

     * @param mCamera
     */
    private fun turnLight(mCamera: Camera?) {
        if (mCamera == null || mCamera.parameters == null || mCamera.parameters.supportedFlashModes == null) {
            return
        }
        val parameters = mCamera.parameters
        val flashMode = mCamera.parameters.flashMode
        val supportedModes = mCamera.parameters.supportedFlashModes
        if (Camera.Parameters.FLASH_MODE_OFF == flashMode && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {//关闭状态
            parameters.flashMode = Camera.Parameters.FLASH_MODE_ON
            mCamera.parameters = parameters
            iv_camera_flash!!.setImageResource(R.mipmap.ic_camera_flash_on)
        } else if (Camera.Parameters.FLASH_MODE_ON == flashMode) {//开启状态
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                iv_camera_flash!!.setImageResource(R.mipmap.ic_camera_flash_auto)
                mCamera.parameters = parameters
            } else if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                iv_camera_flash!!.setImageResource(R.mipmap.ic_camera_flash_off)
                mCamera.parameters = parameters
            }
        } else if (Camera.Parameters.FLASH_MODE_AUTO == flashMode && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            mCamera.parameters = parameters
            iv_camera_flash!!.setImageResource(R.mipmap.ic_camera_flash_off)
        }
    }

    private val mPictureCallback = Camera.PictureCallback { data, camera ->
        bundle = Bundle()
        bundle!!.putByteArray("bytes", data) //将图片字节数据保存在bundle当中，实现数据交换
        SavePicTask(data).execute()
        camera.startPreview() // 拍完照后，重新开始预览
    }

    private inner class SavePicTask internal constructor(private val data: ByteArray) : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            showProgressDialog("处理中")
        }

        override fun doInBackground(vararg params: Void): String? {
            try {
                return saveToSDCard(data)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (!TextUtils.isEmpty(result)) {
                dismissProgressDialog()
                //这边处理接下来的事件
                PhotoProcessActivity.start(mContext!!, result)
                finish()
                LogUtil.d(TAG + result)
            } else {
                ToastUtil.show("拍照失败，请稍后重试！")
            }
        }
    }

    /**
     * 将拍下来的照片存放在SD卡中

     * @param data
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveToSDCard(data: ByteArray): String? {
        val croppedImage: Bitmap

        //获得图片大小
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, options)

        PHOTO_SIZE = if (options.outHeight > options.outWidth) options.outWidth else options.outHeight
        val height = if (options.outHeight > options.outWidth) options.outHeight else options.outWidth
        options.inJustDecodeBounds = false
        val r: Rect
        if (mCurrentCameraId == 1) {
            r = Rect(height - PHOTO_SIZE, 0, height, PHOTO_SIZE)
        } else {
            r = Rect(0, 0, PHOTO_SIZE, PHOTO_SIZE)
        }
        try {
            croppedImage = decodeRegionCrop(data, r)
        } catch (e: Exception) {
            return null
        }

        val imagePath = BitmapUtils.saveToFile(AppConstants.IMAGE_SAVE_PATH, true, croppedImage)
        croppedImage.recycle()
        return imagePath
    }

    private fun decodeRegionCrop(data: ByteArray, rect: Rect): Bitmap {

        var `is`: InputStream? = null
        System.gc()
        var croppedImage: Bitmap? = null
        try {
            `is` = ByteArrayInputStream(data)
            val decoder = BitmapRegionDecoder.newInstance(`is`, false)

            try {
                croppedImage = decoder.decodeRegion(rect, BitmapFactory.Options())
            } catch (e: IllegalArgumentException) {
            }

        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            FileUtils.closeStream(`is`)
        }
        val m = Matrix()
        m.setRotate(90f, (PHOTO_SIZE / 2).toFloat(), (PHOTO_SIZE / 2).toFloat())
        if (mCurrentCameraId == 1) {
            m.postScale(1f, -1f)
        }
        val rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0, PHOTO_SIZE, PHOTO_SIZE, m, true)
        if (croppedImage != null && rotatedImage != croppedImage)
            croppedImage.recycle()
        return rotatedImage
    }

    private val mSurfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            if (cameraInst == null) {
                cameraInst = Camera.open()
                try {
                    cameraInst!!.setPreviewDisplay(holder)
                    initCamera()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            autoFocus()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            try {
                if (cameraInst != null) {
                    cameraInst!!.stopPreview()
                    cameraInst!!.release()
                    cameraInst = null
                }
            } catch (e: Exception) {
                LogUtil.e(TAG + "相机已经关闭")
                //相机已经关闭
            }

        }
    }

    private fun initCamera() {
        parameters = cameraInst!!.parameters
        parameters!!.pictureFormat = ImageFormat.JPEG
        setUpPicSize(parameters!!)
        setUpPreviewSize(parameters!!)
        if (adapterSize != null) parameters!!.setPictureSize(adapterSize!!.width, adapterSize!!.height)
        if (previewSize != null) parameters!!.setPreviewSize(previewSize!!.width, previewSize!!.height)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            parameters!!.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE//1连续对焦
        } else {
            parameters!!.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        setDisplay(parameters!!, cameraInst!!)
        cameraInst!!.parameters = parameters
        cameraInst!!.startPreview()
        cameraInst!!.cancelAutoFocus()//2.如果要实现自动对焦,这一句必须加上

    }

    //控制图像的正确显示方向
    private fun setDisplay(parameters: Camera.Parameters, camera: Camera) {
        if (Build.VERSION.SDK_INT >= 8) {
            setDisplayOrientation(camera, 90)
        } else {
            parameters.setRotation(90)
        }
    }

    //实现的图像的正确显示
    private fun setDisplayOrientation(camera: Camera, i: Int) {
        val downPolymorphic: Method?
        try {
            downPolymorphic = camera.javaClass.getMethod("setDisplayOrientation",
                    *arrayOf<Class<*>>(Integer.TYPE))
            downPolymorphic?.invoke(camera, *arrayOf<Any>(i))
        } catch (e: Exception) {
            Log.e("Came_e", "图像出错")
        }

    }

    private fun setUpPicSize(parameters: Camera.Parameters) {
        if (adapterSize == null) adapterSize = findBestPictureResolution()
    }

    private fun setUpPreviewSize(parameters: Camera.Parameters) {
        if (previewSize == null) previewSize = findBestPreviewResolution()

    }

    /**
     * 找出最适合的预览界面分辨率

     * @return
     */
    private fun findBestPreviewResolution(): Camera.Size {
        val cameraParameters = cameraInst!!.parameters
        val defaultPreviewResolution = cameraParameters.previewSize

        val rawSupportedSizes = cameraParameters.supportedPreviewSizes ?: return defaultPreviewResolution

        // 按照分辨率从大到小排序
        val supportedPreviewResolutions = ArrayList(rawSupportedSizes)
        Collections.sort(supportedPreviewResolutions, Comparator<android.hardware.Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            }
            if (bPixels > aPixels) {
                return@Comparator 1
            }
            0
        })

        val previewResolutionSb = StringBuilder()
        for (supportedPreviewResolution in supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height).append(' ')
        }
        Log.v(TAG, "Supported preview resolutions: " + previewResolutionSb)


        // 移除不符合条件的分辨率
        val screenAspectRatio = ScreenUtils.getScreenWidth(mContext!!).toDouble() / ScreenUtils
                .getScreenHeight(mContext!!).toDouble()
        val it = supportedPreviewResolutions.iterator()
        while (it.hasNext()) {
            val supportedPreviewResolution = it.next()
            val width = supportedPreviewResolution.width
            val height = supportedPreviewResolution.height

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove()
                continue
            }

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            val isCandidatePortrait = width > height
            val maybeFlippedWidth = if (isCandidatePortrait) height else width
            val maybeFlippedHeight = if (isCandidatePortrait) width else height
            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove()
                continue
            }

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == ScreenUtils.getScreenWidth(mContext!!) && maybeFlippedHeight == ScreenUtils.getScreenHeight(mContext!!)) {
                return supportedPreviewResolution
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            val largestPreview = supportedPreviewResolutions[0]
            return largestPreview
        }

        // 没有找到合适的，就返回默认的

        return defaultPreviewResolution
    }

    private fun findBestPictureResolution(): Camera.Size {
        val cameraParameters = cameraInst!!.parameters
        val supportedPicResolutions = cameraParameters.supportedPictureSizes // 至少会返回一个值

        val picResolutionSb = StringBuilder()
        for (supportedPicResolution in supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x').append(supportedPicResolution.height).append(" ")
        }
        Log.d(TAG, "Supported picture resolutions: " + picResolutionSb)

        val defaultPictureResolution = cameraParameters.pictureSize
        Log.d(TAG, "default picture resolution " + defaultPictureResolution.width + "x"
                + defaultPictureResolution.height)

        // 排序
        val sortedSupportedPicResolutions = ArrayList(
                supportedPicResolutions)
        Collections.sort(sortedSupportedPicResolutions, Comparator<android.hardware.Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            }
            if (bPixels > aPixels) {
                return@Comparator 1
            }
            0
        })

        // 移除不符合条件的分辨率
        val screenAspectRatio = ScreenUtils.getScreenWidth(mContext!!).toDouble() / ScreenUtils
                .getScreenHeight(mContext!!).toDouble()
        val it = sortedSupportedPicResolutions.iterator()
        while (it.hasNext()) {
            val supportedPreviewResolution = it.next()
            val width = supportedPreviewResolution.width
            val height = supportedPreviewResolution.height

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            val isCandidatePortrait = width > height
            val maybeFlippedWidth = if (isCandidatePortrait) height else width
            val maybeFlippedHeight = if (isCandidatePortrait) width else height
            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove()
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
        if (!sortedSupportedPicResolutions.isEmpty()) {
            return sortedSupportedPicResolutions[0]
        }

        // 没有找到合适的，就返回默认的
        return defaultPictureResolution
    }

    //实现自动对焦
    private fun autoFocus() {
        object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (cameraInst == null) {
                    return
                }
                cameraInst!!.autoFocus { success, camera ->
                    if (success) {
                        initCamera()//实现相机的参数初始化
                    }
                }
            }
        }
    }

    //定点对焦的代码
    private fun pointFocus(x: Int, y: Int) {
        cameraInst!!.cancelAutoFocus()
        parameters = cameraInst!!.parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y)
        }
        cameraInst!!.parameters = parameters
        autoFocus()
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun showPoint(x: Int, y: Int) {
        if (parameters!!.maxNumMeteringAreas > 0) {
            val areas = ArrayList<Camera.Area>()
            //xy变换了
            val rectY = -x * 2000 / ScreenUtils.getScreenWidth(mContext!!) + 1000
            val rectX = y * 2000 / ScreenUtils.getScreenHeight(mContext!!) - 1000

            val left = if (rectX < -900) -1000 else rectX - 100
            val top = if (rectY < -900) -1000 else rectY - 100
            val right = if (rectX > 900) 1000 else rectX + 100
            val bottom = if (rectY > 900) 1000 else rectY + 100
            val area1 = Rect(left, top, right, bottom)
            areas.add(Camera.Area(area1, 800))
            parameters!!.meteringAreas = areas
        }

        parameters!!.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
    }

    companion object {
        //最小预览界面的分辨率
        private val MIN_PREVIEW_PIXELS = 480 * 320
        //最大宽高比差
        private val MAX_ASPECT_DISTORTION = 0.15
        internal val FOCUS = 1            // 聚焦
        internal val ZOOM = 2            // 缩放

        fun start(context: Context) {
            val starter = Intent(context, CameraActivity::class.java)
            context.startActivity(starter)
        }
    }
}
