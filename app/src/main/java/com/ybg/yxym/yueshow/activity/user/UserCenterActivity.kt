package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.im.extra.UserInfoExtra
import com.ybg.yxym.yb.bean.BangItem
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.GsonUtil
import com.ybg.yxym.yb.utils.MeiLiUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.activity.gift.GiftListActivity
import com.ybg.yxym.yueshow.activity.msg.DateInfoActivity
import com.ybg.yxym.yueshow.adapter.HomeShowAdapter
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.MeiLiImgUtil
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView
import com.ybg.yxym.yueshow.view.bgarefresh.BGANormalRefreshViewHolder
import com.ybg.yxym.yueshow.view.bgarefresh.BGARefreshLayout
import kotlinx.android.synthetic.main.activity_user_center_listview.*
import org.json.JSONObject
import java.util.*

/**
 * 类描述：用户中心
 */
class UserCenterActivity : BaseActivity(), View.OnClickListener {

    private var vDynamic_float: View? = null
    private var tvDynamic_float: TextView? = null
    private var tvDynamicNum_float: TextView? = null

    private var rl_user_wall: RelativeLayout? = null//照片背景
    private var iv_user_logo: CircleImageView? = null//用户头像
    private var iv_level: ImageView? = null//用户等级图片
    private var tv_nickname: TextView? = null
    private var tv_level: TextView? = null
    private var tv_level_name: TextView? = null
    private var tv_sign_name: TextView? = null// 昵称 用户等级 用户等级名称 用户的个性签名
    private var tv_meilizhi: TextView? = null
    private var tv_care: TextView? = null
    private var tv_fans: TextView? = null//美力值 关注 粉丝
    private var iv_miai_1: CircleImageView? = null
    private var iv_miai_2: CircleImageView? = null
    private var iv_miai_3: CircleImageView? = null
    private var rv_miai_1: RelativeLayout? = null
    private var rv_miai_2: RelativeLayout? = null
    private var rv_miai_3: RelativeLayout? = null
    private var tv_go_aimi: TextView? = null//跳转到密爱页面
    private var rl_user_level: RelativeLayout? = null//等级页面

    private var userBase: UserBase? = null

    private var hasMore = true
    private val pageSize = 5//每页取5条
    private var pageNum = 1//页码

    private val TYPE_REFRESH = 0//下拉刷新
    private val TYPE_LOADMORE = 1//上拉加载

    private lateinit var mAdapter: HomeShowAdapter
    private var userShowList: MutableList<YueShow> = ArrayList()

    private var isFollowed = 0
    private var isFriend = 0

    override fun setContentViewId(): Int {
        return R.layout.activity_user_center_listview
    }

    override fun setUpView() {
        vDynamic_float = findViewById(R.id.v_dynamic)
        tvDynamic_float = findViewById(R.id.tv_dynamic) as TextView
        tvDynamicNum_float = findViewById(R.id.tv_dynamic_num) as TextView

        mContext = this@UserCenterActivity
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val headerView = inflater.inflate(R.layout.item_user_list_head, null)
        val floatView = inflater.inflate(R.layout.item_list_user_floating_bar2, null)
        lv_user.addHeaderView(headerView)
        lv_user.addHeaderView(floatView)
        initHeadFloatView(floatView)
        initHeadview(headerView)
        lv_user.setHeaderDividersEnabled(false)
        lv_user.setOnScrollListener(scrollListener)

        rl_fresh_layout.setRefreshViewHolder(BGANormalRefreshViewHolder(mContext!!, true))
        rl_fresh_layout.setDelegate(mDelegate)
    }

    override fun init() {
        setCustomTitle("用户中心")

        if (intent != null) {
            userBase = intent.extras.getSerializable("userBase") as UserBase
            checkFollowStatus()
        }

        mAdapter = HomeShowAdapter(mContext!!)
        mAdapter.setDataList(userShowList)
        lv_user.adapter = mAdapter
    }

    private fun checkFollowStatus() {
        SendRequest.checkFollowStatus(mContext!!, mApplication.token, userBase!!.id,
            object : OkCallback<String>(OkStringParser()){
                override fun onSuccess(code: Int, response: String) {
                    val jsonBean = JSonResultBean.fromJSON(response)
                    if (jsonBean != null && jsonBean.isSuccess) {
                        val json = JSONObject(jsonBean.data)
                        isFollowed = json.getInt("isFollowed")
                        isFriend = json.getInt("isFriend")
                        if (isFollowed > 0 || isFriend > 0) {
                            ll_user_tool.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(e: Throwable) {

                }
            })
    }

    /**
     * @param headview 初始化headerview
     */
    private fun initHeadview(headview: View) {
        rl_user_wall = headview.findViewById(R.id.rl_user_wall) as RelativeLayout
        iv_user_logo = headview.findViewById(R.id.iv_user_logo) as CircleImageView
        tv_nickname = headview.findViewById(R.id.tv_nickname) as TextView
        iv_level = headview.findViewById(R.id.iv_level) as ImageView
        tv_level = headview.findViewById(R.id.tv_level) as TextView
        tv_level_name = headview.findViewById(R.id.tv_level_name) as TextView
        tv_sign_name = headview.findViewById(R.id.tv_sign_name) as TextView
        tv_meilizhi = headview.findViewById(R.id.tv_meilizhi) as TextView
        tv_care = headview.findViewById(R.id.tv_care) as TextView
        tv_fans = headview.findViewById(R.id.tv_fans) as TextView
        iv_miai_1 = headview.findViewById(R.id.iv_mi_ai_first) as CircleImageView
        iv_miai_2 = headview.findViewById(R.id.iv_mi_ai_second) as CircleImageView
        iv_miai_3 = headview.findViewById(R.id.iv_mi_ai_third) as CircleImageView
        rv_miai_1 = headview.findViewById(R.id.rl_mi_ai_first) as RelativeLayout
        rv_miai_2 = headview.findViewById(R.id.rl_mi_ai_second) as RelativeLayout
        rv_miai_3 = headview.findViewById(R.id.rl_mi_ai_third) as RelativeLayout
        tv_go_aimi = headview.findViewById(R.id.tv_go_mi_ai) as TextView
        rl_user_level = headview.findViewById(R.id.rl_user_level) as RelativeLayout
        tv_go_aimi!!.setOnClickListener(this)
        rl_user_level!!.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        userBase?.let {
            setUserInfo(userBase!!)
        }
    }

    private fun setUserInfo(userBase: UserBase) {
        //头像
        val utils = ImageLoaderUtils.instance
        if (TextUtils.isEmpty(userBase.avatar)) {
            //需要修改默认头像时修改此处
            //utils.loadBitmap(userImage, R.mipmap.ic_default_girl);
        } else {
            utils.loadBitmap(iv_user_logo!!, HttpUrl.getImageUrl(userBase.avatar))
        }
        if (TextUtils.isEmpty(userBase.avatarBG)) {
            //navHeader.setBackgroundResource(R.drawable.side_nav_bar);
        } else {
            val bitmap = utils.loadBitmap(userBase.avatarBG)
            rl_user_wall?.background = BitmapDrawable(resources, bitmap)
        }
        //呢称
        tv_nickname?.text = userBase.nickName
        //签名
        tv_sign_name?.text = userBase.ymMemo
        //级别
        val num = MeiLiUtil.getLevelNum(userBase.ml)
        tv_level_name?.text = MeiLiUtil.getLevelName(num)
        tv_level?.text = "LV$num"
        iv_level?.setImageResource(MeiLiImgUtil.getImgId(num))
        tv_meilizhi?.text = "${userBase.ml}"
        //粉丝数
        loadFansNum(userBase)
        //关注数
        loadFollowNum(userBase)
        //蜜爱
        loadMiAi(userBase)
        //加载用户动态
        rl_fresh_layout.beginRefreshing()
        //加载动态数量
        getUserShowNum()
    }

    private var vDynamic: View? = null
    private var tvDynamic: TextView? = null
    private var tvDynamicNum: TextView? = null

    /**
     * @param floatview
     */
    private fun initHeadFloatView(floatview: View) {
        /*动态*/
        vDynamic = floatview.findViewById(R.id.v_dynamic) as View
        tvDynamic = floatview.findViewById(R.id.tv_dynamic) as TextView
        tvDynamicNum = floatview.findViewById(R.id.tv_dynamic_num) as TextView
    }

    //@OnClick({R.id.rl_dynamic, R.id.rl_join_Data, R.id.rl_achievement})
    override fun onClick(view: View) {
        when (view.id) {
            R.id.rl_user_level -> LevelActivity.start(mContext!!)
            R.id.tv_go_mi_ai -> {
                if (userBase != null) {
                    MiAiActivity.start(mContext!!, userBase!!.id, userBase!!.nickName)
                }
            }
            R.id.tv_user_chat -> {
                if (mApplication.hasLogin() && (isFollowed > 0 || isFriend > 0)) {
                    if (userBase != null) {
                        UserInfoExtra.getInstance().openUserChatWin(mContext!!, userBase!!.ymCode, userBase!!.nickName)
                    }
                }
            }
            R.id.tv_user_date -> {
                if (mApplication.hasLogin() && userBase != null) {
                    DateInfoActivity.start(mContext!!, userBase!!.id)
                }
            }
            R.id.tv_user_gift -> {
                if (mApplication.hasLogin() && userBase != null) {
                    GiftListActivity.start(mContext!!, userBase!!.id, userBase!!.ymCode, 1)
                }
            }
        }
    }

    /**
     * listview 滑动监听
     */
    internal var scrollListener: AbsListView.OnScrollListener = object : AbsListView.OnScrollListener {
        override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
        }

        override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            if (firstVisibleItem >= 1) {
                ll_floating!!.visibility = View.VISIBLE
            } else {
                ll_floating!!.visibility = View.GONE
            }
        }
    }

    private fun loadFansNum(userBase: UserBase) {
        SendRequest.getFansNum(mContext!!, userBase.id, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    runOnUiThread {
                        tv_fans?.text = jsonBean.data
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                //不理会
            }
        })
    }

    private fun loadFollowNum(userBase: UserBase) {
        SendRequest.getFollowNum(mContext!!, userBase.id, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    runOnUiThread {
                        tv_care?.text = jsonBean.data
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                //不理会
            }
        })
    }

    private fun loadMiAi(userBase: UserBase) {
        SendRequest.getMiAiBang(mContext!!, "2016-01-01", "2999-12-31", 1, 3, userBase.id, object :
                OkCallback<String>(OkStringParser()) {

            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val list = GsonUtil.createGson().fromJson<List<BangItem>>(jsonBean.data, object :
                            TypeToken<List<BangItem>>() {}.type)
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            ImageLoaderUtils.instance.loadBitmap(iv_miai_1!!, HttpUrl.getImageUrl
                            (first.avatar))
                        } else {
                            rv_miai_1!!.visibility = View.GONE
                        }
                        if (list.size > 1) {
                            val second = list[1]
                            ImageLoaderUtils.instance.loadBitmap(iv_miai_2!!, HttpUrl.getImageUrl
                            (second.avatar))
                        } else {
                            rv_miai_2!!.visibility = View.GONE
                        }
                        if (list.size > 2) {
                            val third = list[2]
                            ImageLoaderUtils.instance.loadBitmap(iv_miai_3!!, HttpUrl.getImageUrl
                            (third.avatar))
                        } else {
                            rv_miai_3!!.visibility = View.GONE
                        }
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                //ToastUtil.show("获取美力榜失败")
            }

        })
    }

    /**
     * 模拟请求网络数据
     */
    private val mShowHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val gson = Gson()
            val jSonResultBean = JSonResultBean.fromJSON(msg.obj.toString())
            var list: List<YueShow> = ArrayList()
            if (jSonResultBean != null && jSonResultBean.isSuccess) {
                list = gson.fromJson<List<YueShow>>(jSonResultBean.data, object : TypeToken<List<YueShow>>() {

                }.type)
            }

            hasMore = list.size == pageSize

            when (msg.what) {
                TYPE_REFRESH -> {
                    rl_fresh_layout.endRefreshing()
                    userShowList.clear()
                    userShowList.addAll(list)
                }
                TYPE_LOADMORE -> {
                    rl_fresh_layout.endLoadingMore()
                    userShowList.addAll(list)
                }
            }
            mAdapter.setDataList(userShowList)
            mAdapter.notifyDataSetChanged()
        }
    }


    /**
     * 监听 刷新或者上拉
     */
    private val mDelegate = object : BGARefreshLayout.BGARefreshLayoutDelegate {
        override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout) {
            pageNum = 1
            getUserShowList()
        }

        override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout): Boolean {
            if (hasMore) {
                pageNum += 1
                getUserShowList()
            } else {
                ToastUtil.show("没有更多数据!")
                return false//不显示更多加载
            }
            return true
        }
    }

    private fun getUserShowList() {
        if (userBase == null) {
            return
        }
        SendRequest.getUserShowList(mContext!!, userBase!!.id.toInt(), pageNum, pageSize, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                if (pageNum == 1) {
                    val message = mShowHandler.obtainMessage()
                    message.what = TYPE_REFRESH
                    message.obj = response
                    mShowHandler.sendMessage(message)
                } else {
                    val message = mShowHandler.obtainMessage()
                    message.what = TYPE_LOADMORE
                    message.obj = response
                    mShowHandler.sendMessage(message)
                }
            }

            override fun onFailure(e: Throwable) {
                rl_fresh_layout.endRefreshing()
            }
        })
    }

    private fun getUserShowNum() {
        if (userBase == null) {
            return
        }
        SendRequest.getUserShowNum(mContext!!, userBase!!.id, object : OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    runOnUiThread {
                        tvDynamicNum?.text = jsonBean.data
                        tvDynamicNum_float?.text = jsonBean.data
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                //do nothing
            }
        })
    }

    companion object {

        /**
         * @param context 跳转到本页面
         */
        fun start(context: Context, userBase: UserBase) {
            val starter = Intent(context, UserCenterActivity::class.java)
            starter.putExtra("userBase", userBase)
            context.startActivity(starter)
        }
    }
}
