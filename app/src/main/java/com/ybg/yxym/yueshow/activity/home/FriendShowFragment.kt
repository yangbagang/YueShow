package com.ybg.yxym.yueshow.activity.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.RelativeLayout

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.adapter.FriendShowAdapter
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.bgarefresh.BGANormalRefreshViewHolder
import com.ybg.yxym.yueshow.view.bgarefresh.BGARefreshLayout

import java.util.ArrayList

/**
 * 类描述：友秀圈
 */
class FriendShowFragment : BaseFragment(), View.OnClickListener {

    private var mListView: ListView? = null
    private var mRefreshLayout: BGARefreshLayout? = null

    private val TYPE_ALL = 0//全部
    private val TYPE_FRIEND = 3//互相关注
    private val TYPE_CARE = 1//我关注

    private var REQUEST_TYPE: Int = 0//请求类型

    /**
     * 一次加载数据的条数
     */
    private var DATA_START = 0
    private var DATA_NUM = 0
    private val DATA_SIZE = 5//每次拉取5条
    private val DATA_TYPE = 0//拉去全部

    private val TYPE_REFRESH = 0//下拉刷新
    private val TYPE_LOADMORE = 1//上拉加载

    private var rlAll: RelativeLayout? = null
    private var rlFans: RelativeLayout? = null
    private var rlCare: RelativeLayout? = null
    private var vAll: View? = null
    private var vFans: View? = null
    private var vCare: View? = null

    private var mAdapter: FriendShowAdapter? = null
    private var hotEntityList: MutableList<YueShow> = ArrayList()

    private var access_token: String? = null
    private var user_id: String? = null

    override fun setContentViewId(): Int {
        return R.layout.fragment_hall_show_friend
    }

    override fun setUpView() {
        mListView = mRootView!!.findViewById(R.id.rv_list_view) as ListView
        mRefreshLayout = mRootView!!.findViewById(R.id.rl_fresh_layout) as BGARefreshLayout

        initHeadView()
        val preferences = activity.getSharedPreferences(AppConstants.SHARE_PREFERENCE_USER, Context.MODE_PRIVATE)
        user_id = preferences.getString(AppConstants.USER_ID, "")
        access_token = preferences.getString(AppConstants.USER_TOKEN, "")
        mRefreshLayout!!.setRefreshViewHolder(BGANormalRefreshViewHolder(mContext!!, true))
        mRefreshLayout!!.setDelegate(mDelegate)
        mRefreshLayout!!.beginRefreshing()
        setResSelector(0)
    }


    override fun init() {
        REQUEST_TYPE = TYPE_ALL
        mAdapter = FriendShowAdapter(mContext!!)
        mAdapter!!.setDataList(hotEntityList)
        mListView!!.adapter = mAdapter
        mListView!!.onItemClickListener = onItemClickListener
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
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_fridend_show_all -> {
                ToastUtil.show("全部" + access_token!!)
                setResSelector(0)
                REQUEST_TYPE = TYPE_ALL
            }
            R.id.rl_fridend_show_fans -> {
                ToastUtil.show("朋友")
                setResSelector(1)
                REQUEST_TYPE = TYPE_FRIEND
            }
            R.id.rl_fridend_show_care -> {
                ToastUtil.show("关注")
                setResSelector(2)
                REQUEST_TYPE = TYPE_CARE
            }
        }
        mRefreshLayout!!.beginRefreshing()
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
            getFriend(user_id!!, access_token!!, REQUEST_TYPE, DATA_START, DATA_SIZE)
        }

        override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout): Boolean {
            DATA_START += DATA_SIZE
            if (DATA_START < DATA_NUM / DATA_SIZE * DATA_SIZE + DATA_SIZE) {
                getFriend(user_id!!, access_token!!, REQUEST_TYPE, DATA_START, DATA_SIZE)
            } else {
                ToastUtil.show("没有更多数据!")
                return false//不显示更多加载
            }
            return true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }


    /**
     * 获取朋友
     */
    private fun getFriend(user_id: String, token: String, typeFriend: Int, start: Int, count: Int) {
        SendRequest.getHomeFriend(activity, user_id, token, typeFriend, 0, start, count, object : OkCallback<String>(OkStringParser()) {
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
     * listview Headview INIT
     */
    private fun initHeadView() {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val headerView = inflater.inflate(R.layout.item_friend_show_list_head, null)
        mListView!!.addHeaderView(headerView)
        rlAll = headerView.findViewById(R.id.rl_fridend_show_all) as RelativeLayout
        rlFans = headerView.findViewById(R.id.rl_fridend_show_fans) as RelativeLayout
        rlCare = headerView.findViewById(R.id.rl_fridend_show_care) as RelativeLayout
        vAll = headerView.findViewById(R.id.v_all) as View
        vFans = headerView.findViewById(R.id.v_fans) as View
        vCare = headerView.findViewById(R.id.v_care) as View
        rlAll!!.setOnClickListener(this)
        rlFans!!.setOnClickListener(this)
        rlCare!!.setOnClickListener(this)
    }

    /**
     * @param type 0:全部   1：朋友   2：关注
     */
    private fun setResSelector(type: Int) {
        when (type) {
            0 -> {
                vAll!!.visibility = View.VISIBLE
                vFans!!.visibility = View.GONE
                vCare!!.visibility = View.GONE
            }
            1 -> {
                vAll!!.visibility = View.GONE
                vFans!!.visibility = View.VISIBLE
                vCare!!.visibility = View.GONE
            }
            2 -> {
                vAll!!.visibility = View.GONE
                vFans!!.visibility = View.GONE
                vCare!!.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        val instance: FriendShowFragment = FriendShowFragment()
    }
}
