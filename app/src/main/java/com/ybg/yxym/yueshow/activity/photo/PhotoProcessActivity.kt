package com.ybg.yxym.yueshow.activity.photo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.ybg.yxym.yb.utils.AppUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.show.PhotoPostActivity
import com.ybg.yxym.yueshow.adapter.FilterAdapter
import com.ybg.yxym.yueshow.adapter.RecyclerBaseAdapter
import com.ybg.yxym.yueshow.adapter.SelectedImageAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.gpuimage.GPUImage
import com.ybg.yxym.yueshow.gpuimage.GPUImageView
import com.ybg.yxym.yueshow.gpuimage.util.GPUImageFilterTools
import com.ybg.yxym.yueshow.utils.BitmapUtils
import com.ybg.yxym.yueshow.utils.FileUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_photo_process.*
import java.io.File
import java.util.*

/**
 * 图片编辑界面
 */
class PhotoProcessActivity : BaseActivity() {

    private var mSmallPreviewImage: Bitmap? = null
    private var mCurrentImage: Bitmap? = null
    private var mFilterAdapter: FilterAdapter? = null//滤镜部分
    private var mFilterList: MutableList<FilterEffect>? = null
    private var mPics: MutableList<String> = ArrayList<String>()
    private var mGPUImageViews: MutableList<GPUImageView> = ArrayList<GPUImageView>()
    private var mIndex = 0

    //选择多图片
    private lateinit var mImageAdapter: SelectedImageAdapter

    override fun setContentViewId(): Int {
        return R.layout.activity_photo_process
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
                    if (pic.startsWith("file://", true)) {
                        mPics.add(pic)
                    } else {
                        mPics.add(String.format("file://%s", pic))
                    }
                    mGPUImageViews.add(GPUImageView(mContext))
                }
                rv_photo_list.visibility = View.VISIBLE
                mImageAdapter.notifyDataSetChanged()
            }
        }

        rv_process_tools.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        rg_process_layout.setOnCheckedChangeListener(mCheckedChangeListener)
        setCustomTitle(getString(R.string.edit))
        mFilterList = EffectService.inst.localFilters as MutableList<FilterEffect>//获取

        setCurrentImage()
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

    private fun setCurrentImage() {
        ImageLoader.getInstance().loadImage(mPics[mIndex], object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                super.onLoadingComplete(imageUri, view, loadedImage)
                mCurrentImage = loadedImage
                ll_photo_process.removeAllViews()
                mGPUImageViews[mIndex].setImage(mCurrentImage)
                ll_photo_process.addView(mGPUImageViews[mIndex])
                asyncLoadSmallImage().execute()
            }
        })
    }

    private inner class asyncLoadSmallImage : AsyncTask<Void, Void, Bitmap>() {

        override fun doInBackground(vararg params: Void): Bitmap {
            return ThumbnailUtils.extractThumbnail(mCurrentImage, 300, 300)
        }

        override fun onPostExecute(bitmap: Bitmap) {
            super.onPostExecute(bitmap)
            mSmallPreviewImage = bitmap
            initFilterToolBar()
        }
    }

    private inner class SaveImageFile : AsyncTask<Unit, Unit, Unit>() {

        private val progress = AppUtil.getProgressDialog(mContext!!, "正在保存...")
        private var savedFiles: ArrayList<String> = ArrayList<String>()

        override fun onPreExecute() {
            super.onPreExecute()
            progress.show()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            if (progress.isShowing) {
                progress.dismiss()
            }
            //启动发布界面
            val list: ArrayList<String> = savedFiles
            PhotoPostActivity.start(mContext!!, list)
            //关闭本窗口
            finish()
        }

        override fun doInBackground(vararg p0: Unit?) {
            for ((index, gpuImageView) in mGPUImageViews.withIndex()) {
                println("index=$index")
                if (gpuImageView.filter != null) {
                    println("begin to process")
                    val file = AppConstants.IMAGE_SAVE_PATH + "/" + System.currentTimeMillis() +
                            "/" + FileUtils.getFileName(mPics[index])
                    savedFiles.add(file)
                    val saveFile = File(file)
                    //val bitmap = gpuImageView.capture()
                    //应用过滤效果
                    println("应用过滤效果")
                    if (gpuImageView.gpuImage == null) {
                        println("gpuImage is null...")
                    }
                    //var bitmap = gpuImageView.gpuImage.bitmapWithFilterApplied
                    val gpuImage = GPUImage(this@PhotoProcessActivity)
                    gpuImage.setImage(mCurrentImage)
                    gpuImage.setFilter(gpuImageView.filter)
                    var bitmap = gpuImage.bitmapWithFilterApplied
                    //缩放尺寸
                    println("缩放尺寸s")
                    bitmap = BitmapUtils.resizeImage(bitmap, 1024, 768)
                    //压缩大小
                    println("压缩大小")
                    bitmap = BitmapUtils.compressImage(bitmap, 500)
                    //保存
                    println("保存")
                    BitmapUtils.saveBitmap(bitmap, saveFile)
                } else {
                    savedFiles.add(mPics[index])
                }
            }
        }

    }

    //底部RadioGroup切换监听
    private val mCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.rb_process_filter//滤镜
            -> {
                rv_process_tools.adapter = mFilterAdapter
                mFilterAdapter!!.notifyDataSetChanged()
            }
            R.id.rb_process_text//文字
            -> {
                //ToastUtil.show("文字")
            }
            R.id.rb_process_stickers//贴纸
            -> {
                //ToastUtil.show("贴纸")
            }
            R.id.rb_process_music//音乐
            -> {
                //ToastUtil.show("音乐")
            }
        }
    }

    //初始化图片选择框
    private fun initSelectedImageBar() {
        rv_photo_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        mImageAdapter = SelectedImageAdapter(mContext!!)
        mImageAdapter.setDataList(mPics)
        mImageAdapter.setOnItemClickListener(object : RecyclerBaseAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                mIndex = position
                setCurrentImage()
            }
        })

        rv_photo_list.adapter = mImageAdapter
    }

    //初始化滤镜
    private fun initFilterToolBar() {
        mFilterAdapter = FilterAdapter(mContext!!, mSmallPreviewImage!!)
        mFilterAdapter!!.setDataList(mFilterList!!)
        mFilterAdapter!!.setOnItemClickListener(object : RecyclerBaseAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val filter = GPUImageFilterTools.createFilterForType(mContext, mFilterList!![position].type)
                mGPUImageViews[mIndex].filter = filter
            }
        })
        rv_process_tools.adapter = mFilterAdapter
        mFilterAdapter!!.notifyDataSetChanged()
    }

    companion object {
        fun start(context: Context, pics: ArrayList<String>) {
            val starter = Intent(context, PhotoProcessActivity::class.java)
            starter.putStringArrayListExtra(IntentExtra.PICTURE_LIST, pics)
            context.startActivity(starter)
        }
    }
}
