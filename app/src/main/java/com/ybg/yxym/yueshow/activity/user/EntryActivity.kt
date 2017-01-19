package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.view.View
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.live.StartLiveActivity
import com.ybg.yxym.yueshow.activity.photo.CameraActivity
import com.ybg.yxym.yueshow.activity.photo.PhotoProcessActivity
import com.ybg.yxym.yueshow.activity.video.VideoProcessActivity
import com.ybg.yxym.yueshow.activity.video.VideoShotActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.utils.AndroidPermissonRequest
import com.ybg.yxym.yueshow.utils.FileUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.gallery.MultiImageSelectorActivity
import kotlinx.android.synthetic.main.activity_entry.*

/**
 * 类描述：拍摄入口
 */
class EntryActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_entry
    }

    override fun setUpView() {
        AndroidPermissonRequest.verifyCameraPermissions(mContext!!)
        setCustomTitle("选择类型")
    }

    override fun init() {
        rl_entry_updata_photo.setOnClickListener(onClickListener)
        rl_entry_updata_video.setOnClickListener(onClickListener)
        rl_entry_photograph.setOnClickListener(onClickListener)
        rl_entry_video_live.setOnClickListener(onClickListener)
        rl_entry_video_play.setOnClickListener(onClickListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentExtra.RequestCode.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data == null) return
            val list = data.getStringArrayListExtra(MultiImageSelectorActivity
                    .EXTRA_RESULT)
            if (list.isEmpty()) {
                ToastUtil.show("你最少需要选择一张图片")
                return
            }
            PhotoProcessActivity.start(mContext!!, list)
            finish()
        } else if (requestCode == IntentExtra.RequestCode.REQUEST_CODE_VIDEO && resultCode ==
                Activity.RESULT_OK) {
            if (data == null) return
            val uri = data.data
            val path = FileUtils.getRealFilePath(mContext!!, uri) ?: return
            VideoProcessActivity.start(mContext!!, path)
            finish()
        } else if (requestCode == IntentExtra.RequestCode.REQUEST_CAPTURE_VIDEO && resultCode ==
                Activity.RESULT_OK) {
            if (data == null) return
            val uri = data.data
            val path = FileUtils.getRealFilePath(mContext!!, uri) ?: return
            VideoProcessActivity.start(mContext!!, path)
            finish()
        }
    }

    /**
     * 点击事件
     */
    private var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        if (!mApplication.hasLogin()) {
            ToastUtil.show("你还没有登录，请先登录。")
        } else {
            when (v.id) {
                R.id.rl_entry_updata_photo//上传图片
                -> {
                    //选择己有的图片上传
                    MultiImageSelectorActivity.start(mContext!!, true, 9, MultiImageSelectorActivity
                            .MODE_MULTI)
                }
                R.id.rl_entry_updata_video//上传视频
                -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "video/*"//选择视频 （mp4 3gp 是android支持的视频格式）
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(intent, IntentExtra.RequestCode.REQUEST_CODE_VIDEO)
                }
                R.id.rl_entry_photograph//拍照
                -> {
                    CameraActivity.start(mContext!!)
                    finish()
                }
                R.id.rl_entry_video_live//直播
                -> {
                    StartLiveActivity.start(mContext!!)
                }
                R.id.rl_entry_video_play//视频
                -> {
                    //自定义视频拍摄界面
                    //VideoShotActivity.start(mContext!!)
                    //finish()
                    //系统拍摄组件
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
                    startActivityForResult(intent, IntentExtra.RequestCode.REQUEST_CAPTURE_VIDEO)
                }
            }
        }
    }

    companion object {


        fun start(context: Context) {
            val starter = Intent(context, EntryActivity::class.java)
            context.startActivity(starter)
        }
    }
}
