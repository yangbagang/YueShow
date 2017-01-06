package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.R.id.iv_photo_list
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
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by yangbagang on 2016/11/17.
 */
class VideoPostActivity : PostShowActivity(){

    private var pic = ""
    private var video = ""

    private var picId = ""
    private var videoId = ""

    override fun setContentViewId(): Int {
        return R.layout.activity_video_post
    }

    override fun postShow() {
        hideKeyboard()
        uploadPic()
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.complete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_finish) {
            postShow()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun uploadPic() {
        SendRequest.uploadFile(mContext!!, "show", File(pic), object : UploadListener(){
            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(e) }
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onSuccess(response: Response) {
                val json = JSONObject(response.body().string())
                picId = json.getString("fid")
                uploadVideo()
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                workInLoopThread {
                    ToastUtil.show("上传图片失败")
                }
            }

            override fun onUIProgress(progress: Progress) {
                runOnUiThread {
                    val progressInt: Int = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                    btn_post_show.text = "上传缩略图(${progressInt}%)"
                }
            }
        })
    }

    private fun uploadVideo() {
        SendRequest.uploadFile(mContext!!, "show", File(video), object : UploadListener(){
            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(e) }
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onSuccess(response: Response) {
                val json = JSONObject(response.body().string())
                videoId = json.getString("fid")
                createShow()
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                workInLoopThread {
                    ToastUtil.show("上传视频失败")
                }
            }

            override fun onUIProgress(progress: Progress) {
                runOnUiThread {
                    val progressInt: Int = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                    btn_post_show.text = "上传视频(${progressInt}%)"
                }
            }
        })
    }

    private fun createShow() {
        val barId = "1"
        SendRequest.createShow(mContext!!, mApplication.token, barId, picId, title, "2",
                object : OkCallback<String>(OkStringParser()) {

                    override fun onSuccess(code: Int, response: String) {
                        val resultBean = JSonResultBean.fromJSON(response)
                        if (resultBean != null && resultBean.isSuccess) {
                            //创建完成，开始添加附件
                            val show = mGson?.fromJson(resultBean.data, YueShow::class.java)
                            appendVideo(show)
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

    private fun appendVideo(show: YueShow?) {
        show?.let {
            SendRequest.addFiles(mContext!!, show.id.toString(), videoId, "2", object :
                    OkCallback<String>(OkStringParser()) {

                override fun onSuccess(code: Int, response: String) {
                    val resultBean = JSonResultBean.fromJSON(response)
                    if (resultBean != null && resultBean.isSuccess) {
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
                    ToastUtil.show("保存文件失败。")
                }

            })
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