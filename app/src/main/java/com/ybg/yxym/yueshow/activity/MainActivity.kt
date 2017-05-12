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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.igexin.sdk.PushManager
import com.pgyersdk.crash.PgyCrashManager
import com.pgyersdk.update.PgyUpdateManager
import com.ybg.yxym.im.extra.UserInfoExtra
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.home.ChartsFragment
import com.ybg.yxym.yueshow.activity.home.HallFragment
import com.ybg.yxym.yueshow.activity.msg.FriendActivity
import com.ybg.yxym.yueshow.activity.user.EntryActivity
import com.ybg.yxym.yueshow.activity.user.LoginActivity
import com.ybg.yxym.yueshow.activity.user.PersonCenterActivity
import com.ybg.yxym.yueshow.adapter.ViewPagerAdapter
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.AndroidPermissonRequest
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CircleImageView
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
    private lateinit var userImage: CircleImageView
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

        if (!AppConstants.isDebug) {
            PgyCrashManager.register(this)
            PgyUpdateManager.register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!AppConstants.isDebug) {
            PgyUpdateManager.unregister()
            PgyCrashManager.unregister()
        }
        UserInfoExtra.getInstance().logout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_msg) {
            //跳转到消息页面
            if (showApplication.hasLogin()) {
                if (showApplication.imHasLogin) {
                    FriendActivity.start(this@MainActivity)
                } else {
                    ToastUtil.show("数据正在载入中，请稍后再试。")
                }
            } else {
                LoginActivity.start(this@MainActivity)
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(false)
//            val startMain = Intent(Intent.ACTION_MAIN) //指定跳到系统桌面
//            startMain.addCategory(Intent.CATEGORY_HOME)
//            startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //清除上一步缓存
//            startActivity(startMain)
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_about) {
            AboutActivity.start(this@MainActivity)
        } else if (id == R.id.nav_msg) {
            if (showApplication.hasLogin()) {
                if (showApplication.imHasLogin) {
                    FriendActivity.start(this@MainActivity)
                } else {
                    ToastUtil.show("数据正在载入中，请稍后再试。")
                }
            } else {
                LoginActivity.start(this@MainActivity)
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setUpView() {
        //左侧菜单
        headView = nav_view.getHeaderView(0)
        userImage = headView.findViewById(R.id.userImage) as CircleImageView
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
            updateClientId()
        }
        headView.setOnClickListener {
            //首先关闭左侧菜单
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            if (showApplication.hasLogin()) {
                PersonCenterActivity.start(this@MainActivity)
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
            R.id.rl_tab_menu_0 -> {
                vp_main_content!!.currentItem = 0
            }
            R.id.rl_tab_menu_1 -> {
                vp_main_content!!.currentItem = 1
            }
            R.id.rl_tab_publish -> {
                if (showApplication.hasLogin()) {
                    EntryActivity.start(this@MainActivity)
                } else {
                    //ToastUtil.show("你还没有登录，请先登录。")
                    LoginActivity.start(this@MainActivity)
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
            utils.loadBitmap(userImage, R.mipmap.default_avatar);
        } else {
            utils.loadBitmap(userImage, HttpUrl.getImageUrl(userBase.avatar))
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
        utils.loadBitmap(userImage, R.mipmap.default_avatar);

        userName.text = "游客"
        userLevel.text = "悦美御秀"
        showApplication.ymCode = ""
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
                    showApplication.ymCode = userBase.ymCode
                    setUserInfo(userBase)
                    //登录第三方IM
                    Thread {
                        UserInfoExtra.getInstance().login(userBase.ymCode, token)
                    }.start()
                } else {
                    if (showApplication.checkNeedLogin(jsonBean?.message ?: "")) {
                        //登录凭证失效，需要重新登录。去除己经相关信息。
                        showApplication.token = ""
                        //LoginActivity.start(this@MainActivity)
                        removeUserInfo()
                    } else {
                        ToastUtil.show(jsonBean?.message ?: "")
                    }
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    fun updateClientId() {
        val userToken = showApplication.token
        val appToken = PushManager.getInstance().getClientid(this@MainActivity)
        if (appToken == null) {
            println("appToken is null")
            return
        }
        SendRequest.updateAppToken(this@MainActivity, userToken, appToken, object :
                OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                //nothing
            }

            override fun onFailure(e: Throwable) {
                //nothing
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
