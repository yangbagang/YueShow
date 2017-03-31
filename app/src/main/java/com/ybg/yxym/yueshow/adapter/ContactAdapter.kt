package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2017/3/30.
 */

class ContactAdapter(private val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater

    private var userList: List<UserBase>? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    fun setData(userList: List<UserBase>) {
        this.userList = userList
    }

    override fun getCount(): Int {
        return userList?.size ?: 0
    }

    override fun getItem(position: Int): UserBase? {
        return if (userList != null) userList!![position] else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var viewholder: ViewHolder? = null
        val user = getItem(position)
        if (view == null) {
            viewholder = ViewHolder()
            view = inflater.inflate(R.layout.list_item_contact, null)
            viewholder.tv_tag = view
                    .findViewById(R.id.tv_contact_tag) as TextView
            viewholder.tv_name = view
                    .findViewById(R.id.tv_contact_name) as TextView
            viewholder.ci_contact = view
                    .findViewById(R.id.ci_contact_avatar) as CircleImageView
            view.tag = viewholder
        } else {
            viewholder = view.tag as ViewHolder
        }
        if (user != null) {// 获取首字母的assii值
            val selection = user.getFirstPY()[0].toInt()
            // 通过首字母的assii值来判断是否显示字母
            val positionForSelection = getPositionForSelection(selection)
            if (position == positionForSelection) {// 相等说明需要显示字母
                viewholder.tv_tag!!.visibility = View.VISIBLE
                viewholder.tv_tag!!.text = user.getFirstPY()
            } else {
                viewholder.tv_tag!!.visibility = View.GONE
            }
            viewholder.tv_name!!.text = user.nickName
            //头像
            val avatar = HttpUrl.getImageUrl(user.avatar)
            ImageLoaderUtils.instance.loadBitmap(viewholder.ci_contact!!, avatar)
        }
        return view!!
    }

    fun getPositionForSelection(selection: Int): Int {
        if (userList == null) {
            return -1
        }
        for (i in userList!!.indices) {
            val pinyin = userList!![i].getFirstPY()
            val first = pinyin.toUpperCase()[0]
            if (first.toInt() == selection) {
                return i
            }
        }
        return -1
    }

    internal inner class ViewHolder {
        var tv_tag: TextView? = null
        var tv_name: TextView? = null
        var ci_contact: CircleImageView? = null
    }

}
