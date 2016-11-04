package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView

import java.util.ArrayList

/**
 * 类描述：个人中心
 */
class UserCenterActivity : BaseActivity(), View.OnClickListener {

    private var lvUser: ListView? = null
    private var llFloating: LinearLayout? = null
    private var vDynamic_float: View? = null
    private var tvDynamic_float: TextView? = null
    private var tvDynamicNum_float: TextView? = null
    internal var vJoinGame_float: View? = null
    private var tvJoinGame_float: TextView? = null
    private var tvJoinGameNum_float: TextView? = null
    private var vAchievement_float: View? = null
    private var tvAchievement_float: TextView? = null
    private var tvAchievementNum_float: TextView? = null

    private var iv_back: ImageView? = null
    private var iv_more: ImageView? = null//返回 一键分享
    private var rl_user_wall: RelativeLayout? = null//照片背景
    private var iv_user_logo: CircleImageView? = null//用户头像
    private var iv_level: ImageView? = null//用户等级图片
    private var tv_nickname: TextView? = null
    private var tv_level: TextView? = null
    private var tv_level_name: TextView? = null
    private var tv_sign_name: TextView? = null// 昵称 用户等级 用户等级名称 用户的个性签名
    private var iv_go_user_info: ImageView? = null
    private var iv_setting: ImageView? = null//跳转到用户资料 //跳转到设置页面
    private var tv_meilizhi: TextView? = null
    private var tv_care: TextView? = null
    private var tv_fans: TextView? = null//美力值 关注 粉丝
    private var iv_miai_1: CircleImageView? = null
    private var iv_miai_2: CircleImageView? = null
    private var iv_miai_3: CircleImageView? = null
    private var tv_go_aimi: TextView? = null//跳转到密爱页面
    private var rl_user_level: RelativeLayout? = null//等级页面

    private val TV_COLOR_SELECT = 0xFF545866.toInt()
    private val TV_COLOR_NORMAL = 0xFFAFB6BC.toInt()
    private val VL_COLOR_SELECT = 0xFFF84F21.toInt()
    private val VL_COLOR_NORMAL = 0xFFecedf3.toInt()

    internal var list1 = ArrayList<String>()
    internal var adapter: ArrayAdapter<*>? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_user_center_listview
    }

    override fun setUpView() {
        lvUser = findViewById(R.id.lv_user) as ListView
        llFloating = findViewById(R.id.ll_floating) as LinearLayout
        vDynamic_float = findViewById(R.id.v_dynamic)
        tvDynamic_float = findViewById(R.id.tv_dynamic) as TextView
        tvDynamicNum_float = findViewById(R.id.tv_dynamic_num) as TextView
        vJoinGame_float = findViewById(R.id.v_join_game)
        tvJoinGame_float = findViewById(R.id.tv_join_game) as TextView
        tvJoinGameNum_float = findViewById(R.id.tv_join_game_num) as TextView
        vAchievement_float = findViewById(R.id.v_achievement)
        tvAchievement_float = findViewById(R.id.tv_achievement) as TextView
        tvAchievementNum_float = findViewById(R.id.tv_achievement_num) as TextView

        mContext = this@UserCenterActivity
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val headerView = inflater.inflate(R.layout.item_user_list_head, null)
        val floatView = inflater.inflate(R.layout.item_list_user_floating_bar, null)
        lvUser!!.addHeaderView(headerView)
        lvUser!!.addHeaderView(floatView)
        initHeadFloatView(floatView)
        initHeadview(headerView)
        lvUser!!.setHeaderDividersEnabled(false)
        lvUser!!.setOnScrollListener(scrollListener)
        list1.add("你好")
        list1.add("同学")
        list1.add("你好")
        list1.add("你好")
        list1.add("同学")
        list1.add("你好")
        list1.add("你好")
        list1.add("同学")
        list1.add("你好")
        list1.add("你好")
        list1.add("同学")
        list1.add("你好")
        list1.add("你好")
        list1.add("同学")
        list1.add("你好")
        list1.add("你好")
        list1.add("同学")
        list1.add("你好")
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list1)
        lvUser!!.adapter = adapter!!
    }

    override fun init() {

    }

    /**
     * @param headview 初始化headerview
     */
    private fun initHeadview(headview: View) {
        iv_back = headview.findViewById(R.id.iv_back) as ImageView
        iv_more = headview.findViewById(R.id.iv_more) as ImageView
        rl_user_wall = headview.findViewById(R.id.rl_user_wall) as RelativeLayout
        iv_user_logo = headview.findViewById(R.id.iv_user_logo) as CircleImageView
        tv_nickname = headview.findViewById(R.id.tv_nickname) as TextView
        iv_level = headview.findViewById(R.id.iv_level) as ImageView
        tv_level = headview.findViewById(R.id.tv_level) as TextView
        tv_level_name = headview.findViewById(R.id.tv_level_name) as TextView
        tv_sign_name = headview.findViewById(R.id.tv_sign_name) as TextView
        iv_go_user_info = headview.findViewById(R.id.iv_go_user_info) as ImageView
        iv_setting = headview.findViewById(R.id.iv_setting) as ImageView
        tv_meilizhi = headview.findViewById(R.id.tv_meilizhi) as TextView
        tv_care = headview.findViewById(R.id.tv_care) as TextView
        tv_fans = headview.findViewById(R.id.tv_fans) as TextView
        iv_miai_1 = headview.findViewById(R.id.iv_aimi_first) as CircleImageView
        iv_miai_2 = headview.findViewById(R.id.iv_aimi_second) as CircleImageView
        iv_miai_3 = headview.findViewById(R.id.iv_aimi_third) as CircleImageView
        tv_go_aimi = headview.findViewById(R.id.tv_go_aimi) as TextView
        rl_user_level = headview.findViewById(R.id.rl_user_level) as RelativeLayout
        iv_back!!.setOnClickListener(this)
        iv_more!!.setOnClickListener(this)
        iv_go_user_info!!.setOnClickListener(this)
        iv_setting!!.setOnClickListener(this)
        tv_go_aimi!!.setOnClickListener(this)
        rl_user_level!!.setOnClickListener(this)
    }


    private var vDynamic: View? = null
    private var vJoinGame: View? = null
    private var vAchievement: View? = null
    private var tvDynamic: TextView? = null
    private var tvDynamicNum: TextView? = null
    private var tvJoinGame: TextView? = null
    private var tvJoinGameNum: TextView? = null
    private var tvAchievementNum: TextView? = null
    private var tvAchievement: TextView? = null
    /**
     * @param floatview
     */
    private fun initHeadFloatView(floatview: View) {
        /*动态*/
        vDynamic = floatview.findViewById(R.id.v_dynamic) as View
        tvDynamic = floatview.findViewById(R.id.tv_dynamic) as TextView
        tvDynamicNum = floatview.findViewById(R.id.tv_dynamic_num) as TextView
        val rlDynamic = floatview.findViewById(R.id.rl_dynamic) as RelativeLayout
        /*比赛*/
        vJoinGame = floatview.findViewById(R.id.v_join_game) as View
        tvJoinGame = floatview.findViewById(R.id.tv_join_game) as TextView
        tvJoinGameNum = floatview.findViewById(R.id.tv_join_game_num) as TextView
        val rlJoinGame = floatview.findViewById(R.id.rl_join_game) as RelativeLayout
        /*成就*/
        vAchievement = floatview.findViewById(R.id.v_achievement) as View
        tvAchievement = floatview.findViewById(R.id.tv_achievement) as TextView
        tvAchievementNum = floatview.findViewById(R.id.tv_achievement_num) as TextView
        val rlAchievement = floatview.findViewById(R.id.rl_achievement) as RelativeLayout
        rlDynamic.setOnClickListener(this)
        rlJoinGame.setOnClickListener(this)
        rlAchievement.setOnClickListener(this)
    }

    //@OnClick({R.id.rl_dynamic, R.id.rl_join_game, R.id.rl_achievement})
    override fun onClick(view: View) {
        when (view.id) {
            R.id.rl_dynamic -> {
                setResSelector(0)
                ToastUtil.show("动态")
            }
            R.id.rl_join_game -> {
                setResSelector(1)
                ToastUtil.show("参加比赛")
            }
            R.id.rl_achievement -> {
                setResSelector(2)
                ToastUtil.show("成就")
            }
            R.id.iv_back -> finish()
            R.id.iv_more -> ToastUtil.show("一键分享")
            R.id.rl_user_level -> LevelActivity.start(mContext!!)
            R.id.iv_go_user_info -> MyInformationActivity.start(mContext!!)
            R.id.iv_setting -> UserSettingActivity.start(mContext!!)
            R.id.tv_go_aimi -> MiAiActivity.start(mContext!!)
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
                llFloating!!.visibility = View.VISIBLE
            } else {
                llFloating!!.visibility = View.GONE
            }
        }
    }

    /**
     * @param number 0:选第一个 1：选第二个 2：选第三个
     */
    private fun setResSelector(number: Int) {
        if (number == 0) {
            tvDynamic!!.setTextColor(TV_COLOR_SELECT)
            tvJoinGame!!.setTextColor(TV_COLOR_NORMAL)
            tvAchievement!!.setTextColor(TV_COLOR_NORMAL)
            vDynamic!!.setBackgroundColor(VL_COLOR_SELECT)
            vJoinGame!!.setBackgroundColor(VL_COLOR_NORMAL)
            vAchievement!!.setBackgroundColor(VL_COLOR_NORMAL)
            tvDynamic_float!!.setTextColor(TV_COLOR_SELECT)
            tvJoinGame_float!!.setTextColor(TV_COLOR_NORMAL)
            tvAchievement_float!!.setTextColor(TV_COLOR_NORMAL)
            vDynamic_float!!.setBackgroundColor(VL_COLOR_SELECT)
            vJoinGame_float!!.setBackgroundColor(VL_COLOR_NORMAL)
            vAchievement_float!!.setBackgroundColor(VL_COLOR_NORMAL)
        } else if (number == 1) {
            tvDynamic!!.setTextColor(TV_COLOR_NORMAL)
            tvJoinGame!!.setTextColor(TV_COLOR_SELECT)
            tvAchievement!!.setTextColor(TV_COLOR_NORMAL)
            vDynamic!!.setBackgroundColor(VL_COLOR_NORMAL)
            vJoinGame!!.setBackgroundColor(VL_COLOR_SELECT)
            vAchievement!!.setBackgroundColor(VL_COLOR_NORMAL)
            tvDynamic_float!!.setTextColor(TV_COLOR_NORMAL)
            tvJoinGame_float!!.setTextColor(TV_COLOR_SELECT)
            tvAchievement_float!!.setTextColor(TV_COLOR_NORMAL)
            vDynamic_float!!.setBackgroundColor(VL_COLOR_NORMAL)
            vJoinGame_float!!.setBackgroundColor(VL_COLOR_SELECT)
            vAchievement_float!!.setBackgroundColor(VL_COLOR_NORMAL)
        } else {
            tvDynamic!!.setTextColor(TV_COLOR_NORMAL)
            tvJoinGame!!.setTextColor(TV_COLOR_NORMAL)
            tvAchievement!!.setTextColor(TV_COLOR_SELECT)
            vDynamic!!.setBackgroundColor(VL_COLOR_NORMAL)
            vJoinGame!!.setBackgroundColor(VL_COLOR_NORMAL)
            vAchievement!!.setBackgroundColor(VL_COLOR_SELECT)
            tvDynamic_float!!.setTextColor(TV_COLOR_NORMAL)
            tvJoinGame_float!!.setTextColor(TV_COLOR_NORMAL)
            tvAchievement_float!!.setTextColor(TV_COLOR_SELECT)
            vDynamic_float!!.setBackgroundColor(VL_COLOR_NORMAL)
            vJoinGame_float!!.setBackgroundColor(VL_COLOR_NORMAL)
            vAchievement_float!!.setBackgroundColor(VL_COLOR_SELECT)
        }
    }

    companion object {

        /**
         * @param context 跳转到本页面
         */
        fun start(context: Context) {
            val starter = Intent(context, UserCenterActivity::class.java)
            context.startActivity(starter)
        }
    }
}
