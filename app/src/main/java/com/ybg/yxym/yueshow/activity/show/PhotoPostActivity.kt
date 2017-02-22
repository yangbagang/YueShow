package com.ybg.yxym.yueshow.activity.show

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.adapter.SelectedImageAdapter
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.UploadListener
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_photo_post.*
import kotlinx.android.synthetic.main.activity_show_post.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by yangbagang on 2016/11/17.
 */
class PhotoPostActivity : PostShowActivity() {

    private lateinit var mImageAdapter: SelectedImageAdapter
    private var mPics: MutableList<String> = ArrayList<String>()
    private var mFiles: MutableList<String> = ArrayList<String>()
    private var mThumbnail = ""
    private var thumbnailId = ""

    override fun setContentViewId(): Int {
        return R.layout.activity_photo_post
    }

    override fun setUpView() {

    }

    override fun init() {
        initSelectedImageBar()

        if (intent != null) {
            val pics = intent.getStringArrayListExtra(IntentExtra.PICTURE_LIST)
            if (pics.isNotEmpty()) {
                mPics.clear()
                for (pic in pics) {
                    if (pic.startsWith("file:")) {
                        mPics.add(pic)
                    } else {
                        mPics.add(String.format("file://%s", pic))
                    }
                }
                mImageAdapter.notifyDataSetChanged()
            }
            mThumbnail = intent.extras.getString("thumbnail")
//            if (!mThumbnail.startsWith("file:")) {
//                mThumbnail = String.format("file://%s", mThumbnail)
//            }
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

    //初始化图片选择框
    private fun initSelectedImageBar() {
        rv_photo_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        mImageAdapter = SelectedImageAdapter(mContext!!)
        mImageAdapter.setDataList(mPics)

        rv_photo_list.adapter = mImageAdapter
    }

    override fun postShow() {
        hideKeyboard()
        //上传缩略图
        btn_post_show.text = "开始上传图片"
        uploadThumbnail()
    }

    private fun uploadThumbnail() {
        println("开始上传缩略图")
        SendRequest.uploadFile(mContext!!, "show", File(mThumbnail), object : UploadListener(){
            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(e) }
            }

            override fun onSuccess(response: Response) {
                val json = JSONObject(response.body().string())
                thumbnailId = json.getString("fid")
                //开始上传图片，图片上传完成后再建美秀。
                println("开始上传缩略图成功")
                println("thumbnailId=$thumbnailId")
                uploadPics()
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                println("开始上传缩略图失败，mThumbnail=$mThumbnail")
                workInLoopThread {
                    ToastUtil.show("上传图片失败")
                }
            }

            override fun onUIProgress(progress: Progress) {

            }
        })
    }

    private fun uploadPics() {
        for (pic in mPics) {
            uploadPic(pic)
        }
        while (mFiles.size < mPics.size) {
            SystemClock.sleep(100)
        }
        createShow()
    }

    private fun uploadPic(pic: String) {
        val file = pic.substring(7)
        //println("开始上传第${picIndex}张图片")
        SendRequest.uploadFile(mContext!!, "show", File(file), object : UploadListener(){
            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(e) }
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onSuccess(response: Response) {
                val json = JSONObject(response.body().string())
                val fileId = json.getString("fid")
                //println("上传第${picIndex}张图片成功")
                mFiles.add(fileId)
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                //println("上传第${picIndex}张图片失败，$file")
                workInLoopThread {
                    ToastUtil.show("上传图片失败")
                }
            }

            override fun onUIProgress(progress: Progress) {
//                runOnUiThread {
//                    val progressInt: Int = (progress.currentBytes * 100 / progress.totalBytes).toInt()
//                    btn_post_show.text = "上传第${picIndex + 1}张(${progressInt}%)"
//                }
            }
        })
    }

    private fun createShow() {
        runOnUiThread {
            btn_post_show.text = "开始创建美秀"
        }
        SendRequest.createShow(mContext!!, mApplication.token, thumbnailId, title, "1",
                object : OkCallback<String>(OkStringParser()) {

            override fun onSuccess(code: Int, response: String) {
                val resultBean = JSonResultBean.fromJSON(response)
                if (resultBean != null && resultBean.isSuccess) {
                    //创建完成，开始添加附件
                    val show = mGson?.fromJson(resultBean.data, YueShow::class.java)
                    appendPics(show)
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

    private fun appendPics(show: YueShow?) {
        val files = mFiles.toTypedArray().joinToString(",")
        show?.let {
            SendRequest.addFiles(mContext!!, show.id.toString(), files, "1", object :
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

        fun start(context: Context, thumbnail: String, pics: ArrayList<String>) {
            val starter = Intent(context, PhotoPostActivity::class.java)
            starter.putStringArrayListExtra(IntentExtra.PICTURE_LIST, pics)
            starter.putExtra("thumbnail", thumbnail)
            context.startActivity(starter)
        }
    }

}