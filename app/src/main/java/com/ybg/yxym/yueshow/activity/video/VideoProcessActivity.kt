package com.ybg.yxym.yueshow.activity.video

import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.AsyncTask
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import com.ybg.yxym.yb.utils.AppUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.show.VideoPostActivity
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.utils.BitmapUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_video_process.*
import java.io.File

class VideoProcessActivity : BaseActivity() {

    private var video = ""
    private var pic = ""

    override fun setContentViewId(): Int {
        return R.layout.activity_video_process
    }

    override fun setUpView() {
        setCustomTitle("视频预览")
    }

    override fun init() {
        if (intent != null) {
            video = intent.extras.getString("videoFile")
            if (video != "") {
                vv_video_process.setVideoPath(video)
                val mediaController = MediaController(this)
                vv_video_process.setMediaController(mediaController)
                mediaController.setMediaPlayer(vv_video_process)
                vv_video_process.requestFocus()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.next, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_next) {
            //保存修改，准备进行下一步。。。
            SaveImageFile().execute()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private inner class SaveImageFile : AsyncTask<Unit, Unit, Unit>() {

        private val progress = AppUtil.getProgressDialog(mContext!!, "正在保存...")
        private var isSupported = false

        override fun onPreExecute() {
            super.onPreExecute()
            progress.show()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            if (progress.isShowing) {
                progress.dismiss()
            }
            if (isSupported) {
                //启动发布界面
                VideoPostActivity.start(mContext!!, pic, video)
            } else {
                ToastUtil.show("视频不存在或格式不支持")
            }
            //关闭本窗口
            finish()
        }

        override fun doInBackground(vararg p0: Unit?) {
            var bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MINI_KIND)
            if (bitmap == null) {
                isSupported = false
                return
            }
            pic = AppConstants.IMAGE_SAVE_PATH + "/" + System.currentTimeMillis() + ".png"
            val saveFile = File(pic)
            //缩放尺寸
            bitmap = BitmapUtils.resizeImage(bitmap, 1024, 768)
            //压缩大小
            bitmap = BitmapUtils.compressImage(bitmap, 500)
            //保存
            BitmapUtils.saveBitmap(bitmap, saveFile)
            isSupported = true
        }

    }

    companion object {
        fun start(context: Context, videoFile: String) {
            val starter = Intent(context, VideoProcessActivity::class.java)
            starter.putExtra("videoFile", videoFile)
            context.startActivity(starter)
        }
    }
}
