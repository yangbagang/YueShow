package com.ybg.yxym.yueshow.activity.show

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.AppUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.adapter.SelectedImageAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.constant.MessageEvent
import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.UploadListener
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.AsyncTaskUtil
import com.ybg.yxym.yueshow.utils.BitmapUtils
import com.ybg.yxym.yueshow.utils.FileUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_photo_post.*
import kotlinx.android.synthetic.main.activity_show_post.*
import okhttp3.Call
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.Serializable
import java.util.*

/**
 * Created by yangbagang on 2016/11/17.
 */
class PhotoPostActivity : PostShowActivity() {

    private lateinit var mImageAdapter: SelectedImageAdapter
    private var mPics: MutableList<String> = ArrayList<String>()
    private var mFiles: MutableList<String> = ArrayList<String>()
    private var mBitmapList: MutableList<Bitmap?> = ArrayList<Bitmap?>()
    private var savedFiles: MutableList<String> = ArrayList<String>()
    private var mThumbnail = ""
    private var thumbnailId = ""

    private var isBackSaving = true
    private var isBackUploading = false

    private var progress: ProgressDialog? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_photo_post
    }

    override fun setUpView() {

    }

    override fun init() {
        initSelectedImageBar()

        if (intent != null) {
            val pics = intent.extras.getSerializable(IntentExtra.PICTURE_LIST) as MutableList<String>
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
            if (mApplication.checkData("bitmapList")) {
                mBitmapList = mApplication.retrieveData("bitmapList") as MutableList<Bitmap?>
            }
            saveFiles()
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
            checkAndPost()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mApplication.removeData("bitmapList")
    }

    //初始化图片选择框
    private fun initSelectedImageBar() {
        rv_photo_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        mImageAdapter = SelectedImageAdapter(mContext!!)
        mImageAdapter.setDataList(mPics)
        mImageAdapter.setBitmapList(mBitmapList)

        rv_photo_list.adapter = mImageAdapter
    }

    private fun saveFiles() {
        isBackSaving = true
        AsyncTaskUtil.startTask({
            for ((index, bitmap) in mBitmapList.withIndex()) {
                if (bitmap != null) {
                    savePic(bitmap, mPics[index], {
                        println("${mPics[index]} has been saved.")
                    })
                } else {
                    var sourceFile = mPics[index]
                    if (sourceFile.startsWith("file:", true)) {
                        sourceFile = sourceFile.substring(5)
                        println("sourceFile=$sourceFile")
                    }
                    val inputStream = FileInputStream(sourceFile)
                    val b = BitmapFactory.decodeStream(inputStream)
                    savePic(b, mPics[index], {
                        println("${mPics[index]} has been saved.")
                    })
                }
            }
            while (savedFiles.size < mPics.size) {
                //println("sleep")
                SystemClock.sleep(100)
            }
        }, {
            isBackUploading = true
            isBackSaving = false

            uploadThumbnail()
        })
    }

    private fun savePic(bitmap: Bitmap, sourceFile: String, call: ()-> Unit) {
        AsyncTaskUtil.startTask({
            val file = AppConstants.IMAGE_SAVE_PATH + "/" + System.currentTimeMillis() +
                    "/" + FileUtils.getFileName(sourceFile)
            val saveFile = File(file)
            //缩放尺寸
            var b = BitmapUtils.resizeImage(bitmap, 1024, 768)
            //压缩大小
            b = BitmapUtils.compressImage(b, 200)
            //保存
            BitmapUtils.saveBitmap(b, saveFile)
            savedFiles.add(file)
        }, call)
    }

    override fun postShow() {
        hideKeyboard()
        progress = AppUtil.getProgressDialog(mContext!!, "正在保存...")
        if (progress != null && !(progress!!.isShowing)) {
            progress!!.show()
        }
        AsyncTaskUtil.startTask({
            while (isBackSaving || isBackUploading) {
                //正在进行后台操作，暂停
                SystemClock.sleep(100)
            }
        }, {
            createShow()
        })
    }

    private fun uploadThumbnail() {
        println("开始上传缩略图")
        SendRequest.uploadPicFile(mContext!!, "show", File(mThumbnail), object : UploadListener(){
            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(e) }
            }

            override fun onSuccess(response: Response) {
                try {
                    val json = JSONObject(response.body().string())
                    thumbnailId = json.getString("fid")
                    //开始上传图片，图片上传完成后再建美秀。
                    println("上传缩略图成功")
                    println("thumbnailId=$thumbnailId")
                    uploadPics()
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                println("上传缩略图失败，mThumbnail=$mThumbnail")
                workInLoopThread {
                    ToastUtil.show("上传图片失败")
                }
            }

            override fun onUIProgress(progress: Progress) {

            }
        })
    }

    private fun uploadPics() {
        for (pic in savedFiles) {
            uploadPic(pic)
        }
        while (mFiles.size < savedFiles.size) {
            SystemClock.sleep(100)
        }
        isBackUploading = false
    }

    private fun uploadPic(pic: String) {
        //val file = pic.substring(7)
        println("开始上传图片: $pic")
        SendRequest.uploadPicFile(mContext!!, "show", File(pic), object : UploadListener(){
            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(e) }
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onSuccess(response: Response) {
                try {
                    val json = JSONObject(response.body().string())
                    val fileId = json.getString("fid")
                    mFiles.add(fileId)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
                workInLoopThread {
                    ToastUtil.show("上传图片失败")
                }
            }

            override fun onUIProgress(progress: Progress) {

            }
        })
    }

    private fun createShow() {
        SendRequest.createShow(mContext!!, mApplication.token, thumbnailId, title, "1", "$price",
                object : OkCallback<String>(OkStringParser()) {

            override fun onSuccess(code: Int, response: String) {
                val resultBean = JSonResultBean.fromJSON(response)
                if (resultBean != null && resultBean.isSuccess) {
                    //创建完成，开始添加附件
                    val show = mGson?.fromJson(resultBean.data, YueShow::class.java)
                    appendPics(show)
                } else {
                    if (progress != null && progress!!.isShowing) {
                        progress!!.dismiss()
                        progress = null
                    }
                    resultBean?.let {
                        checkUserValid(resultBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                if (progress != null && progress!!.isShowing) {
                    progress!!.dismiss()
                    progress = null
                }
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
                        //ToastUtil.show("创建完成。")
                        //finish()
                        EventBus.getDefault().post(MessageEvent(MessageEvent.MESSAGE_SHOW_POST))
                    } else {
                        resultBean?.let {
                            checkUserValid(resultBean.message)
                        }
                    }
                    if (progress != null && progress!!.isShowing) {
                        progress!!.dismiss()
                        progress = null
                    }
                }

                override fun onFailure(e: Throwable) {
                    ToastUtil.show("保存文件失败。")
                    if (progress != null && progress!!.isShowing) {
                        progress!!.dismiss()
                        progress = null
                    }
                }

            })
        }
        ToastUtil.show("创建完成。")
        finish()
    }

    companion object {

        fun start(context: Context, thumbnail: String, pics: MutableList<String>) {
            val starter = Intent(context, PhotoPostActivity::class.java)
            starter.putExtra(IntentExtra.PICTURE_LIST, pics as Serializable)
            starter.putExtra("thumbnail", thumbnail)
            context.startActivity(starter)
        }
    }

}