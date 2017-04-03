package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.view.CircleImageView

/**
 * Created by yangbagang on 2017/3/30.
 */

class GroupMemberAdapter(private val context: Context) : BaseAdapter() {

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
            view = inflater.inflate(R.layout.list_item_member_1, null)
            viewholder.tv_tag = view
                    .findViewById(R.id.tv_contact_tag) as TextView
            viewholder.tv_name = view
                    .findViewById(R.id.tv_contact_name) as TextView
            viewholder.ci_contact = view
                    .findViewById(R.id.ci_contact_avatar) as CircleImageView
            viewholder.iv_checked = view
                    .findViewById(R.id.iv_member_checked) as ImageView
            viewholder.ll_checked = view
                    .findViewById(R.id.ll_member_checked) as LinearLayout
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
            //判断是否选中
            if (user.flag == 1) {
                viewholder.iv_checked!!.visibility = View.VISIBLE
            } else {
                viewholder.iv_checked!!.visibility = View.INVISIBLE
            }
            //选中事件
            viewholder.ll_checked?.setOnClickListener {
                if (user.flag == 1) {
                    user.flag = 0
                    viewholder!!.iv_checked!!.visibility = View.INVISIBLE
                } else {
                    user.flag = 1
                    viewholder!!.iv_checked!!.visibility = View.VISIBLE
                }
            }
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
        var iv_checked: ImageView? = null
        var ll_checked: LinearLayout? = null
    }

}
