package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.constant.MessageEvent
import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.UploadListener
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_show_post.*
import kotlinx.android.synthetic.main.activity_video_post.*
import okhttp3.Call
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by yangbagang on 2016/11/17.
 */
class VideoPostActivity : PostShowActivity() {

    private var pic = ""
    private var video = ""

    private var picId = ""
    private var videoId = ""

    override fun setContentViewId(): Int {
        return R.layout.activity_video_post
    }

    override fun postShow() {
        hideKeyboard()
        Thread {
            while (picId == "") {
                SystemClock.sleep(100)
            }
            createShow()
        }.start()
    }

    override fun setUpView() {

    }

    override fun init() {
        if (intent != null) {
            pic = intent.extras.getString("pic")
            video = intent.extras.getString("video")
            ImageLoaderUtils.instance.loadFileBitmap(iv_photo_list, pic)
        }
        setCustomTitle(getString(R.string.post))
        taskThread.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.complete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_finish) {
            checkAndPost()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createShow() {
        println("title=$title")
        SendRequest.createShow(mContext!!, mApplication.token, picId, title, "2", "$price",
                object : OkCallback<String>(OkStringParser()) {

                    override fun onSuccess(code: Int, response: String) {
                        val resultBean = JSonResultBean.fromJSON(response)
                        if (resultBean != null && resultBean.isSuccess) {
                            //创建完成，开始添加附件
                            val show = mGson?.fromJson(resultBean.data, YueShow::class.java)
                            if (show == null) {
                                ToastUtil.show("建立美秀失败。")
                                return
                            }
                            taskThread.setYueShow(show)
                            //创建完成
                            ToastUtil.show("创建完成。")
                            finish()
                        } else {
                            resultBean?.let {
                                checkUserValid(resultBean.message)
                            }
                        }
                    }

                    override fun onFailure(e: Throwable) {
                        ToastUtil.show("建立美秀失败。")
                    }

                })
    }

    private val taskThread = object : Thread() {

        private var isUploadPic = true
        private var isUploadVideo = true
        private var show: YueShow? = null

        fun setYueShow(yueShow: YueShow) {
            show = yueShow
        }

        override fun run() {
            super.run()
            uploadPic()
            println("begin check...")
            while (isUploadPic || isUploadVideo || show == null) {
                SystemClock.sleep(100)
            }
            println("after check...")
            if (show == null) {
                println("show is null...")
            }
            appendVideo(show)
        }

        private fun uploadPic() {
            println("开始上传缩略图...")
            SendRequest.uploadPicFile(mContext!!, "show", File(pic), object : UploadListener() {
                override fun onFailure(call: Call?, e: IOException?) {
                    e?.let { onFailure(e) }
                }

                override fun onResponse(call: Call?, response: Response?) {
                    response?.let { onSuccess(response) }
                }

                override fun onSuccess(response: Response) {
                    println("上传缩略图成功...")
                    val json = JSONObject(response.body().string())
                    picId = json.getString("fid")
                    isUploadPic = false
                    uploadVideo()
                }

                override fun onFailure(e: Exception) {
                    println("上传缩略图失败...")
                    e.printStackTrace()
                }

                override fun onUIProgress(progress: Progress) {

                }
            })
        }

        private fun uploadVideo() {
            println("开始上传视频文件...")
            SendRequest.uploadVideoFile(mContext!!, "show", File(video), object : UploadListener() {
                override fun onFailure(call: Call?, e: IOException?) {
                    e?.let { onFailure(e) }
                }

                override fun onResponse(call: Call?, response: Response?) {
                    response?.let { onSuccess(response) }
                }

                override fun onSuccess(response: Response) {
                    println("上传视频文件完成...")
                    val json = JSONObject(response.body().string())
                    videoId = json.getString("fid")
                    isUploadVideo = false
                }

                override fun onFailure(e: Exception) {
                    println("上传视频文件失败...")
                    e.printStackTrace()
                }

                override fun onUIProgress(progress: Progress) {

                }
            })
        }

        private fun appendVideo(show: YueShow?) {
            println("准备添加视频附件...")
            show?.let {
                SendRequest.addFiles(mContext!!, show.id.toString(), videoId, "2", object :
                        OkCallback<String>(OkStringParser()) {

                    override fun onSuccess(code: Int, response: String) {
                        val resultBean = JSonResultBean.fromJSON(response)
                        if (resultBean != null && resultBean.isSuccess) {
                            //创建完成
                            println("添加视频文件完成...")
                            EventBus.getDefault().post(MessageEvent(MessageEvent.MESSAGE_SHOW_POST))
                        } else {
                            resultBean?.let {
                                checkUserValid(resultBean.message)
                            }
                        }
                    }

                    override fun onFailure(e: Throwable) {
                        //ToastUtil.show("保存文件失败。")
                        e.printStackTrace()
                        println("添加视频文件失败...")
                    }

                })
            }
        }
    }

    companion object {

        fun start(context: Context, pic: String, video: String) {
            val starter = Intent(context, VideoPostActivity::class.java)
            starter.putExtra("pic", pic)
            starter.putExtra("video", video)
            context.startActivity(starter)
        }
    }

}