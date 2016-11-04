package com.ybg.yxym.yueshow.activity.home

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.adapter.FreshShowAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.bgarefresh.BGANormalRefreshViewHolder
import com.ybg.yxym.yueshow.view.bgarefresh.BGARefreshLayout

import java.util.ArrayList

class FreshFragment : BaseFragment() {

    private var mListView: ListView? = null
    private var mRefreshLayout: BGARefreshLayout? = null

    /**
     * userid
     */
    private var user_id: String? = null
    /**
     * 从第0条数据开始
     */
    private var DATA_START = 0
    /**
     * 总的据的条数
     */
    private var DATA_NUM = 0
    /**
     * 每次数据的条数
     */
    private val DATA_SIZE = 5
    /**
     * 返回数据的类型 1直播，2是图片，3是视频，0全部
     */
    private val DATA_TYPE = 0
    private val TYPE_REFRESH = 0//下拉刷新
    private val TYPE_LOADMORE = 1//上拉加载

    private var mAdapter: FreshShowAdapter? = null
    private var hotEntityList: MutableList<YueShow> = ArrayList()

    override fun setContentViewId(): Int {
        LogUtil.d("Hot----" + "加载布局-------最鲜")
        return R.layout.fragment_hall_show_fresh
    }

    override fun setUpView() {
        mContext = activity
        getSharePref()

        mListView = mRootView!!.findViewById(R.id.rv_list_view) as ListView
        mRefreshLayout = mRootView!!.findViewById(R.id.rl_fresh_layout) as BGARefreshLayout
    }

    override fun init() {
        mAdapter = FreshShowAdapter(mContext!!)
        mAdapter!!.setDataList(hotEntityList)
        mListView!!.adapter = mAdapter

        mRefreshLayout!!.setRefreshViewHolder(BGANormalRefreshViewHolder(mContext!!, true))
        mRefreshLayout!!.setDelegate(mDelegate)
        mRefreshLayout!!.beginRefreshing()
        mListView!!.onItemClickListener = onItemClickListener
    }

    /**
     * ListView ITEM 点击事件
     */
    private val onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        if (hotEntityList[position].type == 2 || hotEntityList[position].type == 3) {
            //TODO
            //HomeShowItemDetailActivity.start(mContext, hotEntityList.get(position).getLive()
            //        , hotEntityList.get(position).getUser());
        } else {
            ToastUtil.show("直播已经结束")
        }
    }


    private fun getSharePref() {
        val preferences = mContext!!.getSharedPreferences(AppConstants.SHARE_PREFERENCE_USER, Context.MODE_PRIVATE)
        user_id = preferences.getString(AppConstants.USER_ID, "")
    }

    /**
     * @param context
     * *
     * @param userid
     * *
     * @param type
     * *
     * @param start
     * *
     * @param count
     */
    private fun getHomeNewInfo(context: Context, userid: String, type: Int, start: Int, count: Int) {
        SendRequest.getHomeNew(context, userid, type, start, count, object : OkCallback<String>(OkStringParser()) {
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
                if (mRefreshLayout != null) {
                    mRefreshLayout!!.endRefreshing()
                }
            }
        })
    }


    /**
     * 模拟请求网络数据
     */
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val gson = Gson()
            val jSonResultBean = gson.fromJson(msg.obj.toString(), JSonResultBean::class.java)
            var list: List<YueShow> = ArrayList()
            if (jSonResultBean.isSuccess) {
                list = gson.fromJson<List<YueShow>>(jSonResultBean.data, object : TypeToken<List<YueShow>>() {

                }.type)
            }
            DATA_NUM = list.size

            when (msg.what) {
                0 -> {
                    if (mRefreshLayout != null) {
                        mRefreshLayout!!.endRefreshing()
                    }
                    hotEntityList.clear()
                    hotEntityList.addAll(list)
                }
                1 -> {
                    if (mRefreshLayout != null) {
                        mRefreshLayout!!.endLoadingMore()
                    }
                    hotEntityList.addAll(list)
                }
            }
            mAdapter!!.setDataList(hotEntityList)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * 监听 刷新或者上拉
     */
    private val mDelegate = object : BGARefreshLayout.BGARefreshLayoutDelegate {
        override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout) {
            DATA_START = 0
            getHomeNewInfo(mContext!!, user_id!!, DATA_TYPE, DATA_START, DATA_SIZE)
        }

        override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout): Boolean {
            DATA_START += DATA_SIZE
            if (DATA_START < DATA_NUM / DATA_SIZE * DATA_SIZE + DATA_SIZE) {
                getHomeNewInfo(mContext!!, user_id!!, DATA_TYPE, DATA_START, DATA_SIZE)
            } else {
                ToastUtil.show("没有更多数据!")
                return false
            }
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        val instance: FreshFragment = FreshFragment()
    }

}
