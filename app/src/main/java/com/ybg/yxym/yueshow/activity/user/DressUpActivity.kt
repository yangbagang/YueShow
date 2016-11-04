package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.DressUpAdapter

import java.util.ArrayList

/**
 * 类描述：装扮页面
 */
class DressUpActivity : BaseActivity() {

    private var gridview: GridView? = null

    internal var stringList: MutableList<String> = ArrayList<String>()
    private var dressUpAdapter: DressUpAdapter? = null
    override fun setContentViewId(): Int {
        return R.layout.activity_dress_up
    }

    override fun setUpView() {
        gridview = findViewById(R.id.gridview) as GridView
        mContext = this@DressUpActivity
        setCustomTitle("装扮背景")
    }

    override fun init() {
        initData()
        dressUpAdapter = DressUpAdapter(this)
        dressUpAdapter!!.setData(stringList)
        gridview!!.adapter = dressUpAdapter
        gridview!!.onItemClickListener = onItemClickListener
    }

    private val onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l -> Toast.makeText(mContext, "第" + i + "条", Toast.LENGTH_SHORT).show() }

    private fun initData() {
        stringList.clear()
        for (i in 0..29) {
            stringList.add("" + i)
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, DressUpActivity::class.java)
            context.startActivity(starter)
        }
    }
}
