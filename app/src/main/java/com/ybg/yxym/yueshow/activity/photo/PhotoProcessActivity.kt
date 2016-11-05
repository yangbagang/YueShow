package com.ybg.yxym.yueshow.activity.photo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.RadioGroup
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.FilterAdapter
import com.ybg.yxym.yueshow.adapter.RecyclerBaseAdapter
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.gpuimage.util.GPUImageFilterTools
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_photo_process.*

/**
 * 图片编辑界面
 */
class PhotoProcessActivity : BaseActivity() {

    private var mSmallPreviewImage: Bitmap? = null
    private var mCurrentImage: Bitmap? = null
    private var mPath: String? = null
    private var mFilterAdapter: FilterAdapter? = null//滤镜部分
    private var mFilterList: MutableList<FilterEffect>? = null

    protected override fun setContentViewId(): Int {
        return R.layout.activity_photo_process
    }

    protected override fun setUpView() {

    }

    protected override fun init() {
        if (getIntent() != null) {
            mPath = String.format("file://%s", getIntent().getStringExtra(IntentExtra.EXTRA_PHOTO_RESULT))
        }
        rv_process_tools.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        rg_process_layout.setOnCheckedChangeListener(mCheckedChangeListener)
        setCustomTitle(getString(R.string.edit))
        mFilterList = EffectService.inst.localFilters as MutableList<FilterEffect>//获取
        ImageLoader.getInstance().loadImage(mPath, object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                super.onLoadingComplete(imageUri, view, loadedImage)
                mCurrentImage = loadedImage
                iv_photo_process.setImage(loadedImage)
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

    //初始化滤镜
    private fun initFilterToolBar() {
        mFilterAdapter = FilterAdapter(mContext!!, mSmallPreviewImage!!)
        mFilterAdapter!!.setDataList(mFilterList!!)
        mFilterAdapter!!.setOnItemClickListener(object : RecyclerBaseAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val filter = GPUImageFilterTools.createFilterForType(mContext, mFilterList!![position.toInt()].type)
                iv_photo_process.filter = filter
            }
        })
    }

    companion object {

        fun start(context: Context, result: String) {
            val starter = Intent(context, PhotoProcessActivity::class.java)
            starter.putExtra(IntentExtra.EXTRA_PHOTO_RESULT, result)
            context.startActivity(starter)
        }
    }
}
