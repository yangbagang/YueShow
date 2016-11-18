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
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.FilterAdapter
import com.ybg.yxym.yueshow.adapter.RecyclerBaseAdapter
import com.ybg.yxym.yueshow.adapter.SelectedImageAdapter
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.gpuimage.util.GPUImageFilterTools
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_photo_process.*
import java.util.*

/**
 * 图片编辑界面
 */
class PhotoProcessActivity : BaseActivity() {

    private var mSmallPreviewImage: Bitmap? = null
    private var mCurrentImage: Bitmap? = null
    private var mPath: String? = null
    private var mFilterAdapter: FilterAdapter? = null//滤镜部分
    private var mFilterList: MutableList<FilterEffect>? = null
    private var mPics: MutableList<String> = ArrayList<String>()

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
            //拍照入口
            mPath = String.format("file://%s", intent.getStringExtra(IntentExtra.EXTRA_PHOTO_RESULT))
            //文件选择入口
            val pics = intent.getStringArrayListExtra(IntentExtra.PICTURE_LIST)
            if (pics.isNotEmpty()) {
                mPics.clear()
                for (pic in pics) {
                    mPics.add(String.format("file://%s", pic))
                }
                mPath = mPics.first()
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
            //下一步
            ToastUtil.show("下一步。。。")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setCurrentImage() {
        ImageLoader.getInstance().loadImage(mPath, object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                super.onLoadingComplete(imageUri, view, loadedImage)
                mCurrentImage = loadedImage
                iv_photo_process.setImage(mCurrentImage)
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
                ToastUtil.show("文字")
            }
            R.id.rb_process_stickers//贴纸
            -> {
                ToastUtil.show("贴纸")
            }
            R.id.rb_process_music//音乐
            -> {
                ToastUtil.show("音乐")
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
                mPath = mPics[position]
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
                iv_photo_process.filter = filter
            }
        })
        rv_process_tools.adapter = mFilterAdapter
        mFilterAdapter!!.notifyDataSetChanged()
    }

    companion object {

        fun start(context: Context, result: String) {
            val starter = Intent(context, PhotoProcessActivity::class.java)
            starter.putExtra(IntentExtra.EXTRA_PHOTO_RESULT, result)
            context.startActivity(starter)
        }

        fun start(context: Context, pics: ArrayList<String>) {
            val starter = Intent(context, PhotoProcessActivity::class.java)
            starter.putStringArrayListExtra(IntentExtra.PICTURE_LIST, pics)
            context.startActivity(starter)
        }
    }
}
