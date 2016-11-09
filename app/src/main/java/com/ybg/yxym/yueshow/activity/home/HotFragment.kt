package com.ybg.yxym.yueshow.activity.home

import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.adapter.HotShowAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.bgarefresh.BGANormalRefreshViewHolder
import com.ybg.yxym.yueshow.view.bgarefresh.BGARefreshLayout

import java.util.ArrayList

class HotFragment : BaseFragment() {

    private lateinit var mListView: ListView
    private lateinit var mRefreshLayout: BGARefreshLayout
    /**
     * 一次加载数据的条数
     */
    private var DATA_START = 0
    private var DATA_NUM = 0
    private val DATA_SIZE = 5//每次拉取5条
    private val DATA_TYPE = 0//拉去全部

    private val TYPE_REFRESH = 0//下拉刷新
    private val TYPE_LOADMORE = 1//上拉加载

    private lateinit var mAdapter: HotShowAdapter
    private var hotEntityList: MutableList<YueShow> = ArrayList()

    override fun setContentViewId(): Int {
        return R.layout.fragment_hall_show_hot
    }

    override fun setUpView() {
        mListView = mRootView!!.findViewById(R.id.rv_list_view) as ListView
        mRefreshLayout = mRootView!!.findViewById(R.id.rl_fresh_layout) as BGARefreshLayout

        mRefreshLayout.setRefreshViewHolder(BGANormalRefreshViewHolder(mContext!!, true))
        mRefreshLayout.setDelegate(mDelegate)
        mRefreshLayout.beginRefreshing()
    }

    override fun init() {
        mAdapter = HotShowAdapter(mContext!!)
        mAdapter.setDataList(hotEntityList)
        mListView.adapter = mAdapter
        mListView.onItemClickListener = onItemClickListener
    }

    /**
     * ListView ITEM 点击事件
     */
    private val onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        if (hotEntityList[position].type == 2 || hotEntityList[position].type == 3) {
            //TODO
            //HomeShowItemDetailActivity.start(mContext,hotEntityList.get(position).getLive(),
            //        hotEntityList.get(position).getUser());
        } else {
            ToastUtil.show("直播已经结束")
        }
    }


    /**
     * 模拟请求网络数据
     */
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val gson = Gson()
            val jSonResultBean = JSonResultBean.fromJSON(msg.obj.toString())
            var list: List<YueShow> = ArrayList()
            if (jSonResultBean != null && jSonResultBean.isSuccess) {
                list = gson.fromJson<List<YueShow>>(jSonResultBean.data, object : TypeToken<List<YueShow>>() {

                }.type)
            }
            DATA_NUM = list.size

            when (msg.what) {
                0 -> {
                    mRefreshLayout.endRefreshing()
                    hotEntityList.clear()
                    hotEntityList.addAll(list)
                }
                1 -> {
                    mRefreshLayout.endLoadingMore()
                    hotEntityList.addAll(list)
                }
            }
            mAdapter.setDataList(hotEntityList)
            mAdapter.notifyDataSetChanged()
        }
    }


    /**
     * 监听 刷新或者上拉
     */
    private val mDelegate = object : BGARefreshLayout.BGARefreshLayoutDelegate {
        override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout) {
            DATA_START = 0
            getHotInfolist(mContext!!, DATA_TYPE, DATA_START, DATA_SIZE)
        }

        override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout): Boolean {
            DATA_START += DATA_SIZE
            if (DATA_START < DATA_NUM / DATA_SIZE * DATA_SIZE + DATA_SIZE) {
                getHotInfolist(mContext!!, DATA_TYPE, DATA_START, DATA_SIZE)
            } else {
                ToastUtil.show("没有更多数据!")
                return false//不显示更多加载
            }
            return true
        }
    }

    /**
     * @param context
     * *
     * @param type
     * *
     * @param start
     * *
     * @param count
     */
    private fun getHotInfolist(context: Context, type: Int, start: Int, count: Int) {
        SendRequest.getHomeHot(context, type, start, count, object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                if (DATA_START == 0) {
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
            }

            override fun onFailure(e: Throwable) {
                mRefreshLayout.endRefreshing()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        val instance: HotFragment = HotFragment()
    }
}
