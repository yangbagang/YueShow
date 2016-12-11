package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.BangItem
import com.ybg.yxym.yb.bean.JSonResultBean

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.adapter.BangAdapter
import com.ybg.yxym.yueshow.adapter.MiAiAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.bgarefresh.BGANormalRefreshViewHolder
import com.ybg.yxym.yueshow.view.bgarefresh.BGARefreshLayout
import java.util.*

/**
 * 类描述：密爱页面
 */
class MiAiActivity : BaseActivity() {

    var beginTime = "2016-01-01"
    var endTime = "2999-12-31"

    var userId = 0L
    var userName = ""

    private lateinit var mListView: ListView
    private lateinit var mRefreshLayout: BGARefreshLayout

    private var hasMore = true
    private val pageSize = 10//每页取10条
    private var pageNum = 1//页码

    private val TYPE_REFRESH = 0//下拉刷新
    private val TYPE_LOADMORE = 1//上拉加载

    private lateinit var mAdapter: MiAiAdapter
    private var bangEntityList: MutableList<BangItem> = ArrayList()

    override fun setContentViewId(): Int {
        return R.layout.activity_mi_ai
    }

    override fun setUpView() {
        setCustomTitle("爱蜜情深")

        mListView = findViewById(R.id.rv_list_view) as ListView
        mRefreshLayout = findViewById(R.id.rl_fresh_layout) as BGARefreshLayout

        mRefreshLayout.setRefreshViewHolder(BGANormalRefreshViewHolder(mContext!!, true))
        mRefreshLayout.setDelegate(mDelegate)
        mRefreshLayout.beginRefreshing()
    }

    override fun init() {
        if (intent != null) {
            userId = intent.extras.getLong("userId")
            userName = intent.extras.getString("userName")
        }
        mAdapter = MiAiAdapter(mContext!!)
        mAdapter.setDataList(bangEntityList)
        mListView.adapter = mAdapter
    }

    fun loadData() {
        SendRequest.getMiAiBang(mContext!!, beginTime, endTime, pageNum, pageSize, userId, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                if (pageNum == 1) {
                    val message = miHandler.obtainMessage()
                    message.what = TYPE_REFRESH
                    message.obj = response
                    miHandler.sendMessage(message)
                } else {
                    val message = miHandler.obtainMessage()
                    message.what = TYPE_LOADMORE
                    message.obj = response
                    miHandler.sendMessage(message)
                }
                mRefreshLayout.endRefreshing()
            }

            override fun onFailure(e: Throwable) {
                mRefreshLayout.endRefreshing()
            }

        })
    }

    private val miHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val gson = Gson()
            val jSonResultBean = JSonResultBean.fromJSON(msg.obj.toString())
            var list: List<BangItem> = ArrayList()
            if (jSonResultBean != null && jSonResultBean.isSuccess) {
                list = gson.fromJson<List<BangItem>>(jSonResultBean.data, object : TypeToken<List<BangItem>>() {

                }.type)
            }

            hasMore = list.size < pageSize

            when (msg.what) {
                0 -> {
                    mRefreshLayout.endRefreshing()
                    bangEntityList.clear()
                    bangEntityList.addAll(list)
                }
                1 -> {
                    mRefreshLayout.endLoadingMore()
                    bangEntityList.addAll(list)
                }
            }
            mAdapter.setDataList(bangEntityList)
            mAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 监听 刷新或者上拉
     */
    private val mDelegate = object : BGARefreshLayout.BGARefreshLayoutDelegate {
        override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout) {
            pageNum = 1
            loadData()
        }

        override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout): Boolean {
            if (hasMore) {
                pageNum += 1
                loadData()
            } else {
                ToastUtil.show("没有更多数据!")
                return false
            }
            return true
        }

    }

    companion object {

        fun start(context: Context, userId: Long, userName: String) {
            val starter = Intent(context, MiAiActivity::class.java)
            starter.putExtra("userId", userId)
            starter.putExtra("userName", userName)
            context.startActivity(starter)
        }
    }

}
