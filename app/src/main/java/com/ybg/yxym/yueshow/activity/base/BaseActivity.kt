package com.ybg.yxym.yueshow.activity.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserBase
import com.ybg.yxym.yb.bean.UserInfo
import com.ybg.yxym.yueshow.activity.MainActivity
import com.ybg.yxym.yueshow.activity.user.LoginActivity
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.constant.MessageEvent
import com.ybg.yxym.yueshow.http.OkHttpProxy
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class BaseActivity : AppCompatActivity() {

    protected var TAG = javaClass.simpleName
    protected var mContext: Activity? = null
    protected var mGson: Gson? = null
    private val mRootView: View? = null

    protected var mApplication: ShowApplication = ShowApplication.instance!!

    protected var mPageNum: Int = 0//下拉刷新和上拉加载时翻页的记录
    protected var mEventBus: EventBus? = null
    protected var mHandler: Handler? = null
    protected var mProgressDialog: ProgressDialog? = null

    protected abstract fun setContentViewId(): Int

    /**
     * 初始化View的方法
     */
    protected abstract fun setUpView()

    /**
     * 初始化方法
     */
    protected abstract fun init()

    fun <T : View> `$`(@IdRes idRes: Int): T {
        return findViewById(idRes) as T
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setContentViewId())
        mContext = this
        mEventBus = EventBus.getDefault()
        mEventBus!!.register(this)
        mHandler = Handler()
        mGson = GsonBuilder().serializeNulls().create()
        setContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mEventBus != null) mEventBus!!.unregister(this)
        OkHttpProxy.cancel(mContext!!)
    }

    private fun setContent() {
        setUpView()
        init()
    }

    /*隐藏虚拟键盘*/
    protected fun hideKeyboard() {
        val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mContext!!.window.peekDecorView().windowToken,
        InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    /*显示虚拟键盘*/
    protected fun showKeyboard() {
        val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mContext!!.window.peekDecorView().windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS)
    }

    protected fun getTextViewText(view: TextView?): CharSequence {
        if (view == null) return ""
        return view.text
    }

    protected fun getTextViewString(view: TextView): String {
        return getTextViewText(view).toString().trim()
    }

    //防止字体随系统设置发生改变
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    @Subscribe
    fun onEvent(event: MessageEvent) {//处理EventBus 发送的消息的方法,具体操作在子类实现

    }

    private val mLocalCallBack = Handler.Callback { msg ->
        onHandler(msg)
        false
    }

    protected open fun onHandler(msg: Message) {//处理handler发送的消息,具体操作在子类中实现

    }

    protected fun showProgressDialog(msg: String) {
        var msg = msg
        if (mProgressDialog != null && mProgressDialog!!.isShowing) return
        if (mProgressDialog == null) mProgressDialog = ProgressDialog(mContext)
        if (TextUtils.isEmpty(msg)) msg = "加载中..."
        mProgressDialog!!.setMessage(msg)
        mProgressDialog!!.show()
    }

    protected fun dismissProgressDialog() {
        if (mProgressDialog == null) return
        if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun setCustomTitle(title: String) {
        try {
            val actionBar = supportActionBar
            actionBar!!.setDisplayHomeAsUpEnabled(true)
            actionBar.title = title
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected fun checkUserValid(msg: String) {
        if (mApplication.checkNeedLogin(msg)) {
            mApplication.token = ""
            MainActivity.instance?.removeUserInfo()
            LoginActivity.start(mContext!!)
        } else {
            ToastUtil.show(msg)
        }
    }

    protected fun loadUserBase(call: (UserBase) -> Unit) {
        SendRequest.getUserBase(mContext!!, mApplication.token, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val mGson = GsonBuilder().serializeNulls().create()
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //成功
                    val userBase = mGson.fromJson(jsonBean.data, UserBase::class.java)
                    call(userBase)
                } else {
                    jsonBean?.let {
                        checkUserValid(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取用户信息失败。")
            }
        })
    }

    protected fun loadUserInfo(call: (UserInfo) -> Unit) {
        SendRequest.getUserInfo(mContext!!, mApplication.token, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val mGson = GsonBuilder().serializeNulls().create()
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    //成功
                    val userInfo = mGson.fromJson(jsonBean.data, UserInfo::class.java)
                    call(userInfo)
                } else {
                    jsonBean?.let {
                        checkUserValid(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取用户信息失败。")
            }
        })
    }
}
