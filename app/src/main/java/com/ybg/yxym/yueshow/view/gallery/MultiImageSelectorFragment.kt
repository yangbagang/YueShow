package com.ybg.yxym.yueshow.view.gallery

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.ListPopupWindow
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.picasso.Picasso
import com.ybg.yxym.yueshow.view.gallery.adapter.FolderAdapter
import com.ybg.yxym.yueshow.view.gallery.adapter.ImageGridAdapter
import com.ybg.yxym.yueshow.view.gallery.bean.Folder
import com.ybg.yxym.yueshow.view.gallery.bean.Image
import com.ybg.yxym.yueshow.view.gallery.utils.FileUtils
import com.ybg.yxym.yueshow.view.gallery.utils.TimeUtils
import java.io.File
import java.util.*

class MultiImageSelectorFragment : Fragment() {

    // 结果数据
    private var resultList: ArrayList<String>? = ArrayList()
    // 文件夹数据
    private val mResultFolder = ArrayList<Folder>()

    // 图片Grid
    private var mGridView: GridView? = null
    private var mCallback: Callback? = null

    private var mImageAdapter: ImageGridAdapter? = null
    private var mFolderAdapter: FolderAdapter? = null

    private var mFolderPopupWindow: ListPopupWindow? = null

    // 时间线
    private var mTimeLineText: TextView? = null
    // 类别
    private var mCategoryText: TextView? = null
    // 预览按钮
    private var mPreviewBtn: Button? = null
    // 底部View
    private var mPopupAnchorView: View? = null

    private var mDesireImageCount: Int = 0

    private var hasFolderGened = false
    private var mIsShowCamera = false

    private var mGridWidth: Int = 0
    private var mGridHeight: Int = 0

    private var mTmpFile: File? = null

    private val mLoaderCallback = object : LoaderManager.LoaderCallbacks<Cursor> {

        private val IMAGE_PROJECTION = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images
                .Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID)

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
            if (id == LOADER_ALL) {
                val cursorLoader = CursorLoader(activity,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC")
                return cursorLoader
            } else if (id == LOADER_CATEGORY) {
                val cursorLoader = CursorLoader(activity,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args?.getString("path") + "%'", null,
                        IMAGE_PROJECTION[2] + " DESC")
                return cursorLoader
            }

            return null
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            if (data != null) {
                val images = ArrayList<Image>()
                val count = data.count
                if (count > 0) {
                    data.moveToFirst()
                    do {
                        val path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                        val name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                        val dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                        val image = Image(path, name, dateTime)
                        images.add(image)
                        if (!hasFolderGened) {
                            // 获取文件夹名称
                            val imageFile = File(path)
                            val folderFile = imageFile.parentFile
                            val folder = Folder()
                            folder.name = folderFile.name
                            folder.path = folderFile.absolutePath
                            folder.cover = image
                            if (!mResultFolder.contains(folder)) {
                                val imageList = ArrayList<Image>()
                                imageList.add(image)
                                folder.images = imageList
                                mResultFolder.add(folder)
                            } else {
                                // 更新
                                val f = mResultFolder[mResultFolder.indexOf(folder)]
                                f.images!!.add(image)
                            }
                        }

                    } while (data.moveToNext())

                    mImageAdapter!!.setData(images)

                    // 设定默认选择
                    if (resultList != null && resultList!!.size > 0) {
                        mImageAdapter!!.setDefaultSelected(resultList!!)
                    }

                    mFolderAdapter!!.setData(mResultFolder)
                    hasFolderGened = true

                }
            }
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {

        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mCallback = activity as Callback?
        } catch (e: ClassCastException) {
            throw ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...")
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_multi_image, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 选择图片数量
        mDesireImageCount = arguments.getInt(EXTRA_SELECT_COUNT)

        // 图片选择模式
        val mode = arguments.getInt(EXTRA_SELECT_MODE)

        // 默认选择
        if (mode == MODE_MULTI) {
            val tmp = arguments.getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST)
            if (tmp != null && tmp.size > 0) {
                resultList = tmp
            }
        }

        // 是否显示照相机
        mIsShowCamera = arguments.getBoolean(EXTRA_SHOW_CAMERA, true)
        mImageAdapter = ImageGridAdapter(activity, mIsShowCamera)
        // 是否显示选择指示器
        mImageAdapter!!.showSelectIndicator(mode == MODE_MULTI)

        mPopupAnchorView = view!!.findViewById(R.id.footer)

        mTimeLineText = view.findViewById(R.id.timeline_area) as TextView
        // 初始化，先隐藏当前timeline
        mTimeLineText!!.visibility = View.GONE

        mCategoryText = view.findViewById(R.id.category_btn) as TextView
        // 初始化，加载所有图片
        mCategoryText!!.setText(R.string.folder_all)
        mCategoryText!!.setOnClickListener {
            if (mFolderPopupWindow == null) {
                createPopupFolderList(mGridWidth, mGridHeight)
            }

            if (mFolderPopupWindow!!.isShowing) {
                mFolderPopupWindow!!.dismiss()
            } else {
                mFolderPopupWindow!!.show()
                var index = mFolderAdapter!!.selectIndex
                index = if (index == 0) index else index - 1
                mFolderPopupWindow!!.listView!!.setSelection(index)
            }
        }

        mPreviewBtn = view.findViewById(R.id.preview) as Button
        // 初始化，按钮状态初始化
        if (resultList == null || resultList!!.size <= 0) {
            mPreviewBtn!!.setText(R.string.preview)
            mPreviewBtn!!.isEnabled = false
        }
        mPreviewBtn!!.setOnClickListener { }

        mGridView = view.findViewById(R.id.grid) as GridView
        mGridView!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, state: Int) {

                val picasso = Picasso.with(activity)
                if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE || state ==
                        AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(activity)
                } else {
                    picasso.pauseTag(activity)
                }

                if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 停止滑动，日期指示器消失
                    mTimeLineText!!.visibility = View.GONE
                } else if (state == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mTimeLineText!!.visibility = View.VISIBLE
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (mTimeLineText!!.visibility == View.VISIBLE) {
                    val index = if (firstVisibleItem + 1 == view.adapter.count) view.adapter.count - 1 else firstVisibleItem + 1
                    val image = view.adapter.getItem(index) as Image
                    if (image != null) {
                        mTimeLineText!!.text = TimeUtils.formatPhotoDate(image.path)
                    }
                }
            }
        })
        mGridView!!.adapter = mImageAdapter
        mGridView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {

                val width = mGridView!!.width
                val height = mGridView!!.height

                mGridWidth = width
                mGridHeight = height

                val desireSize = resources.getDimensionPixelOffset(R.dimen.px_214)
                val numCount = width.toFloat() / desireSize
                val columnSpace = resources.getDimensionPixelOffset(R.dimen.px_4)
                val columnWidth = (width - columnSpace * (numCount - 1)) / numCount
                LogUtil.d(TAG + " width" + width + "空隙" + columnSpace + "列数" + numCount + "预计列宽" +
                        desireSize + "计算结果为" + columnWidth)
                mImageAdapter!!.setItemSize(columnWidth.toInt())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    mGridView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })
        mGridView!!.setOnItemClickListener { adapterView, view, i, l ->
            if (mImageAdapter!!.isShowCamera) {
                // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                if (i == 0) {
                    showCameraAction()
                } else {
                    // 正常操作
                    selectImageFromGrid(adapterView.adapter.getItem(i) as Image, mode)
                }
            } else {
                // 正常操作
                selectImageFromGrid(adapterView.adapter.getItem(i) as Image, mode)
            }
        }

        mFolderAdapter = FolderAdapter(activity)
    }

    /**
     * 创建弹出的ListView
     */
    private fun createPopupFolderList(width: Int, height: Int) {
        mFolderPopupWindow = ListPopupWindow(activity)
        mFolderPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mFolderPopupWindow!!.setAdapter(mFolderAdapter)
        mFolderPopupWindow!!.setContentWidth(width)
        mFolderPopupWindow!!.width = width
        mFolderPopupWindow!!.height = height * 5 / 8
        mFolderPopupWindow!!.anchorView = mPopupAnchorView
        mFolderPopupWindow!!.isModal = true
        mFolderPopupWindow!!.setOnItemClickListener { adapterView, view, i, l ->
            mFolderAdapter!!.selectIndex = i

            val index = i
            val v = adapterView

            Handler().postDelayed({
                mFolderPopupWindow!!.dismiss()

                if (index == 0) {
                    activity.supportLoaderManager.restartLoader(LOADER_ALL, null, mLoaderCallback)
                    mCategoryText!!.setText(R.string.folder_all)
                    if (mIsShowCamera) {
                        mImageAdapter!!.isShowCamera = true
                    } else {
                        mImageAdapter!!.isShowCamera = false
                    }
                } else {
                    val folder = v.adapter.getItem(index) as Folder
                    if (null != folder) {
                        mImageAdapter!!.setData(folder.images)
                        mCategoryText!!.text = folder.name
                        // 设定默认选择
                        if (resultList != null && resultList!!.size > 0) {
                            mImageAdapter!!.setDefaultSelected(resultList!!)
                        }
                    }
                    mImageAdapter!!.isShowCamera = false
                }

                // 滑动到最初始位置
                mGridView!!.smoothScrollToPosition(0)
            }, 100)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 首次加载所有图片
        //new LoadImageTask().execute();
        activity.supportLoaderManager.initLoader(LOADER_ALL, null, mLoaderCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback!!.onCameraShot(mTmpFile!!)
                    }
                }
            } else {
                if (mTmpFile != null && mTmpFile!!.exists()) {
                    mTmpFile!!.delete()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        Log.d(TAG, "on change")

        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow!!.isShowing) {
                mFolderPopupWindow!!.dismiss()
            }
        }

        mGridView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {

                val height = mGridView!!.height

                val desireSize = resources.getDimensionPixelOffset(R.dimen.px_214)
                Log.d(TAG, "Desire Size = " + desireSize)
                val numCount = mGridView!!.width / desireSize
                Log.d(TAG, "Grid Size = " + mGridView!!.width)
                Log.d(TAG, "num count = " + numCount)
                val columnSpace = resources.getDimensionPixelOffset(R.dimen.px_4)
                val columnWidth = (mGridView!!.width - columnSpace * (numCount - 1)) / numCount
                mImageAdapter!!.setItemSize(columnWidth)

                if (mFolderPopupWindow != null) {
                    mFolderPopupWindow!!.height = height * 5 / 8
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    mGridView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })

        super.onConfigurationChanged(newConfig)

    }

    /**
     * 选择相机
     */
    private fun showCameraAction() {
        // 跳转到系统照相机
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activity.packageManager) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtils.createTmpFile(activity)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile))
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        } else {
            Toast.makeText(activity, R.string.msg_no_camera, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 选择图片操作

     * @param image
     */
    private fun selectImageFromGrid(image: Image, mode: Int) {
        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {
                if (resultList!!.contains(image.path)) {
                    resultList!!.remove(image.path)
                    if (resultList!!.size != 0) {
                        mPreviewBtn!!.isEnabled = true
                        mPreviewBtn!!.text = resources.getString(R.string.preview) + "(" + resultList!!.size + ")"
                    } else {
                        mPreviewBtn!!.isEnabled = false
                        mPreviewBtn!!.setText(R.string.preview)
                    }
                    if (mCallback != null) {
                        mCallback!!.onImageUnselected(image.path)
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount == resultList!!.size) {
                        Toast.makeText(activity, R.string.msg_amount_limit, Toast.LENGTH_SHORT).show()
                        return
                    }

                    resultList!!.add(image.path)
                    mPreviewBtn!!.isEnabled = true
                    mPreviewBtn!!.text = resources.getString(R.string.preview) + "(" + resultList!!.size + ")"
                    if (mCallback != null) {
                        mCallback!!.onImageSelected(image.path)
                    }
                }
                mImageAdapter!!.select(image)
            } else if (mode == MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    mCallback!!.onSingleImageSelected(image.path)
                }
            }
        }
    }

    /**
     * 回调接口
     */
    interface Callback {
        fun onSingleImageSelected(path: String)

        fun onImageSelected(path: String)

        fun onImageUnselected(path: String)

        fun onCameraShot(imageFile: File)
    }

    companion object {

        private val TAG = "MultiImageSelector"

        /**
         * 最大图片选择次数，int类型
         */
        val EXTRA_SELECT_COUNT = "max_select_count"
        /**
         * 图片选择模式，int类型
         */
        val EXTRA_SELECT_MODE = "select_count_mode"
        /**
         * 是否显示相机，boolean类型
         */
        val EXTRA_SHOW_CAMERA = "show_camera"
        /**
         * 默认选择的数据集
         */
        val EXTRA_DEFAULT_SELECTED_LIST = "default_result"
        /**
         * 单选
         */
        val MODE_SINGLE = 0
        /**
         * 多选
         */
        val MODE_MULTI = 1
        // 不同loader定义
        private val LOADER_ALL = 0
        private val LOADER_CATEGORY = 1
        // 请求加载系统照相机
        private val REQUEST_CAMERA = 100
    }
}
