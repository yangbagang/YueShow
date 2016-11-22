package com.ybg.yxym.yueshow.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.home.ChartsFragment
import com.ybg.yxym.yueshow.activity.home.HallFragment
import com.ybg.yxym.yueshow.activity.user.EntryActivity
import com.ybg.yxym.yueshow.activity.user.LoginActivity
import com.ybg.yxym.yueshow.activity.user.UserCenterActivity
import com.ybg.yxym.yueshow.adapter.ViewPagerAdapter
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.AndroidPermissonRequest
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val showApplication = ShowApplication.instance!!

    private var mCurrentTab = 0
    private val mTabViews = ArrayList<View>()
    private val mTabTexts = ArrayList<View>()
    private lateinit var mAdapter: ViewPagerAdapter
    private val mList = ArrayList<Fragment>()

    //左侧菜单
    private lateinit var headView: View
    private lateinit var userImage: ImageView
    private lateinit var userName: TextView
    private lateinit var userLevel: TextView
    private lateinit var navHeader: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        setUpView()
        init()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setUpView() {
        //左侧菜单
        headView = nav_view.getHeaderView(0)
        userImage = headView.findViewById(R.id.userImage) as ImageView
        userName = headView.findViewById(R.id.userName) as TextView
        userLevel = headView.findViewById(R.id.userLevel) as TextView
        navHeader = headView.findViewById(R.id.navHeader) as LinearLayout

        mTabViews.add(iv_tab_menu_0!!)
        mTabViews.add(iv_tab_menu_1!!)
        mTabTexts.add(tv_tab_menu_0!!)
        mTabTexts.add(tv_tab_menu_1!!)
        iv_tab_menu_0!!.isSelected = true
        tv_tab_menu_0!!.isSelected = true
        /** 6.0 动态申请权限 外部存储和相机 */
        AndroidPermissonRequest.verifyStoragePermissions(this@MainActivity)
    }

    private fun init() {
        instance = this
        mAdapter = ViewPagerAdapter(supportFragmentManager)
        mList.add(HallFragment.newInstance())
        mList.add(ChartsFragment.newInstance())
        mAdapter.setDataList(mList)
        vp_main_content!!.adapter = mAdapter
        vp_main_content!!.addOnPageChangeListener(mPageChangeListener)

        if (showApplication.hasLogin()) {
            loadUserInfo()
        }
        headView.setOnClickListener {
            //首先关闭左侧菜单
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            if (showApplication.hasLogin()) {
                UserCenterActivity.start(this@MainActivity)
            } else {
                LoginActivity.start(this@MainActivity)
            }
        }
    }


    private val mPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            LogUtil.d(TAG + " onPageSelected " + position)
            changeTabState(position)
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.rl_tab_menu_0 -> vp_main_content!!.currentItem = 0
            R.id.rl_tab_menu_1 -> vp_main_content!!.currentItem = 1
            R.id.rl_tab_publish -> {
                if (showApplication.hasLogin()) {
                    EntryActivity.start(this@MainActivity)
                } else {
                    ToastUtil.show("你还没有登录，请登录后再尝试。")
                }
            }
        }
    }

    /*修改点击是下面四个tab状态*/
    private fun changeTabState(position: Int) {
        if (mCurrentTab != position) {
            for (i in mTabViews.indices) {
                if (position == i) {
                    mTabViews[i].isSelected = true
                    mTabTexts[i].isSelected = true
                } else {
                    mTabViews[i].isSelected = false
                    mTabTexts[i].isSelected = false
                }
            }
            mCurrentTab = position
        }
    }

    private fun setUserInfo(userBase: UserBase) {
        val utils = ImageLoaderUtils.instance
        if (TextUtils.isEmpty(userBase.avatar)) {
            //utils.loadBitmap(userImage, R.mipmap.ic_default_girl);
        } else {
            utils.loadBitmap(userImage, userBase.avatar)
        }
        if (TextUtils.isEmpty(userBase.avatarBG)) {
            //navHeader.setBackgroundResource(R.drawable.side_nav_bar);
        } else {
            val bitmap = utils.loadBitmap(userBase.avatarBG)
            navHeader.background = BitmapDrawable(resources, bitmap)
        }
        userName.text = userBase.nickName
        userLevel.text = userBase.ymMemo
    }

    fun removeUserInfo() {
        val utils = ImageLoaderUtils.instance
        utils.loadBitmap(userImage, R.mipmap.ic_default_girl);

        userName.text = "游客"
        userLevel.text = "悦美御秀"
    }

    fun loadUserInfo() {
        val token = showApplication.token
        SendRequest.getUserBase(this@MainActivity, token, object : OkCallback<String>(OkStringParser()) {

            override fun onSuccess(code: Int, response: String) {
                val mGson = GsonBuilder().serializeNulls().create()
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //成功
                    val userBase = mGson.fromJson(jsonBean.data, UserBase::class.java)
                    setUserInfo(userBase)
                } else {
                    if (showApplication.checkNeedLogin(jsonBean?.message ?: "")) {
                        showApplication.token = ""
                        LoginActivity.start(this@MainActivity)
                    } else {
                        ToastUtil.show(jsonBean?.message ?: "")
                    }
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    companion object {

        private val TAG = "MainActivity:"

        var instance: MainActivity? = null

        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }
}
