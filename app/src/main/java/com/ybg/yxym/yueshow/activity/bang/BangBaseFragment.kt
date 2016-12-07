package com.ybg.yxym.yueshow.activity.bang

import android.os.Handler
import android.os.Message
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.BangItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.adapter.BangAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.bgarefresh.BGANormalRefreshViewHolder
import com.ybg.yxym.yueshow.view.bgarefresh.BGARefreshLayout
import java.util.*

/**
 * Created by yangbagang on 2016/12/4.
 */
abstract class BangBaseFragment: BaseFragment() {

    private val titles = arrayOf("美力值", "美力值", "活力值", "人气值", "亲善值")
    open var type = 1
    open var beginTime = "2016-01-01"
    open var endTime = "2999-12-31"

    private lateinit var mListView: ListView
    private lateinit var mRefreshLayout: BGARefreshLayout

    private var hasMore = true
    private val pageSize = 5//每页取5条
    private var pageNum = 1//页码

    private val TYPE_REFRESH = 0//下拉刷新
    private val TYPE_LOADMORE = 1//上拉加载

    private lateinit var mAdapter: BangAdapter
    private var bangEntityList: MutableList<BangItem> = ArrayList()

    abstract fun initParams()

    override fun setContentViewId(): Int {
        return R.layout.fragment_bang_list
    }

    override fun setUpView() {
        mListView = mRootView!!.findViewById(R.id.rv_list_view) as ListView
        mRefreshLayout = mRootView!!.findViewById(R.id.rl_fresh_layout) as BGARefreshLayout

        initParams()

        mRefreshLayout.setRefreshViewHolder(BGANormalRefreshViewHolder(mContext!!, true))
        mRefreshLayout.setDelegate(mDelegate)
        mRefreshLayout.beginRefreshing()
    }

    override fun init() {
        mAdapter = BangAdapter(mContext!!)
        mAdapter.setDataList(bangEntityList)
        mAdapter.setScoreName(titles[type])
        mListView.adapter = mAdapter
    }

    fun loadRuiMeiBang() {
        ToastUtil.show("正在开发中，更多功能敬请期待...")
    }

    fun loadMeiLiBang() {
        SendRequest.getRuiMeiBang(mContext!!, beginTime, endTime, pageNum, pageSize, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                if (pageNum == 1) {
                    val message = mHandler.obtainMessage()
                    message.what = TYPE_REFRESH
                    message.obj = response
                    mHandler.sendMessage(message)
                } else {
                    val message = mHandler.obtainMessage()
                    message.what = TYPE_LOADMORE
                    message.obj = response
                    mHandler.sendMessage(message)
                }
                mRefreshLayout.endRefreshing()
            }

            override fun onFailure(e: Throwable) {
                mRefreshLayout.endRefreshing()
            }

        })
    }

    fun loadHuoLiBang() {
        SendRequest.getHuoLiBang(mContext!!, beginTime, endTime, pageNum, pageSize, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                if (pageNum == 1) {
                    val message = mHandler.obtainMessage()
                    message.what = TYPE_REFRESH
                    message.obj = response
                    mHandler.sendMessage(message)
                } else {
                    val message = mHandler.obtainMessage()
                    message.what = TYPE_LOADMORE
                    message.obj = response
                    mHandler.sendMessage(message)
                }
                mRefreshLayout.endRefreshing()
            }

            override fun onFailure(e: Throwable) {
                mRefreshLayout.endRefreshing()
            }

        })
    }

    fun loadRenQiBang() {
        SendRequest.getRenQiBang(mContext!!, beginTime, endTime, pageNum, pageSize, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                if (pageNum == 1) {
                    val message = mHandler.obtainMessage()
                    message.what = TYPE_REFRESH
                    message.obj = response
                    mHandler.sendMessage(message)
                } else {
                    val message = mHandler.obtainMessage()
                    message.what = TYPE_LOADMORE
                    message.obj = response
                    mHandler.sendMessage(message)
                }
                mRefreshLayout.endRefreshing()
            }

            override fun onFailure(e: Throwable) {
                mRefreshLayout.endRefreshing()
            }

        })
    }

    fun loadHaoQiBang() {
        ToastUtil.show("正在开发中，更多功能敬请期待...")
    }

    private val mHandler = object : Handler() {
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

        private fun loadData() {
            when(type) {
                0 -> {
                    ToastUtil.show("没有更多数据!")
                }
                1 -> loadMeiLiBang()
                2 -> loadHuoLiBang()
                3 -> loadRenQiBang()
                4 -> {
                    ToastUtil.show("没有更多数据!")
                }
            }
        }
    }

}