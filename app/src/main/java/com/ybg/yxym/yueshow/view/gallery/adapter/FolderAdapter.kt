package com.ybg.yxym.yueshow.view.gallery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.view.gallery.bean.Folder

import java.util.ArrayList

class FolderAdapter(private val mContext: Context) : BaseAdapter() {
    private val mInflater: LayoutInflater

    private var mFolders: MutableList<Folder>? = ArrayList()

    internal var mImageSize: Int = 0

    internal var lastSelected = 0

    init {
        mInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mImageSize = mContext.resources.getDimensionPixelOffset(R.dimen.px_128)
    }

    /**
     * 设置数据集

     * @param folders
     */
    fun setData(folders: MutableList<Folder>?) {
        if (folders != null && folders.size > 0) {
            mFolders = folders
        } else {
            mFolders!!.clear()
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mFolders!!.size + 1
    }

    override fun getItem(i: Int): Folder? {
        if (i == 0) return null
        return mFolders!![i - 1]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        val holder: ViewHolder?
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_folder, viewGroup, false)
            holder = ViewHolder(view)
        } else {
            holder = view.tag as ViewHolder
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.text = "所有图片"
                holder.size.setText("${totalImageSize}张")
                if (mFolders!!.size > 0) {
                    val f = mFolders!![0]
                    ImageLoaderUtils.instance.loadFileBitmap(holder.cover, f.cover!!.path, mImageSize, mImageSize)
                }
            } else {
                holder.bindData(getItem(i)!!)
            }
            if (lastSelected == i) {
                holder.indicator.visibility = View.VISIBLE
            } else {
                holder.indicator.visibility = View.INVISIBLE
            }
        }
        return view!!
    }

    private val totalImageSize: Int
        get() {
            var result = 0
            if (mFolders != null && mFolders!!.size > 0) {
                for (f in mFolders!!) {
                    result += f.images!!.size
                }
            }
            return result
        }

    var selectIndex: Int
        get() = lastSelected
        set(i) {
            if (lastSelected == i) return

            lastSelected = i
            notifyDataSetChanged()
        }

    internal inner class ViewHolder(view: View) {
        var cover: ImageView
        var name: TextView
        var size: TextView
        var indicator: ImageView

        init {
            cover = view.findViewById(R.id.cover) as ImageView
            name = view.findViewById(R.id.name) as TextView
            size = view.findViewById(R.id.size) as TextView
            indicator = view.findViewById(R.id.indicator) as ImageView
            view.tag = this
        }

        fun bindData(data: Folder) {
            name.text = data.name
            size.setText("${data.images!!.size}张")
            // 显示图片
            ImageLoaderUtils.instance.loadFileBitmap(cover, data.cover!!.path, mImageSize, mImageSize)
        }
    }

}
