package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.MainActivity
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.app.ShowApplication
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.RegularUtil
import com.ybg.yxym.yueshow.utils.ToastUtil

import org.json.JSONException
import org.json.JSONObject

/**
 * 用户登陆界面
 */
class LoginActivity : BaseActivity() {

    private var m_etLoginMobile: EditText? = null
    private var m_etLoginPassword: EditText? = null
    private var m_btnLogin: Button? = null
    private var m_tvForgetPassword: TextView? = null
    private var m_tvRegisterNow: TextView? = null
    private var tvOpenPlatform: TextView? = null
    private var m_ivLoginQQ: ImageView? = null
    private var m_ivLoginWeChat: ImageView? = null
    private var m_ivLoginSina: ImageView? = null

    private var mMobile: String? = null//手机号or悦美号
    private var mPassword: String? = null//登录密码

    private var mPlatform: SHARE_MEDIA? = null
    private var mShareAPI: UMShareAPI? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_login
    }

    override fun setUpView() {
        m_etLoginMobile = findViewById(R.id.et_login_mobile) as EditText
        m_etLoginPassword = findViewById(R.id.et_login_password) as EditText
        m_btnLogin = findViewById(R.id.btn_login) as Button
        m_tvForgetPassword = findViewById(R.id.tv_forget_password) as TextView
        m_tvRegisterNow = findViewById(R.id.tv_register_now) as TextView
        tvOpenPlatform = findViewById(R.id.tv_open_platform) as TextView
        m_ivLoginQQ = findViewById(R.id.iv_login_qq) as ImageView
        m_ivLoginWeChat = findViewById(R.id.iv_login_wechat) as ImageView
        m_ivLoginSina = findViewById(R.id.iv_login_sina) as ImageView
    }

    override fun init() {
        mShareAPI = UMShareAPI.get(mContext)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_login_qq//QQ登陆
            -> {
                LogUtil.d(TAG + ": QQ登录")
                mPlatform = SHARE_MEDIA.QQ
                mShareAPI!!.doOauthVerify(mContext, mPlatform, mAuthListener)
            }
            R.id.iv_login_wechat//微信登录
            -> {
                LogUtil.d(TAG + ": 微信登录")
                mPlatform = SHARE_MEDIA.WEIXIN
                mShareAPI!!.doOauthVerify(mContext, mPlatform, mAuthListener)
            }
            R.id.iv_login_sina//微博登录
            -> {
                LogUtil.d(TAG + ": 微博登录")
                mPlatform = SHARE_MEDIA.SINA
                mShareAPI!!.doOauthVerify(mContext, mPlatform, mAuthListener)
            }
            R.id.btn_login//手机号or悦美号登录
            -> {
                LogUtil.d(TAG + ": 手机号or悦美号登录")
                mMobile = getTextViewString(m_etLoginMobile!!)
                mPassword = getTextViewString(m_etLoginPassword!!)
                if (TextUtils.isEmpty(mMobile) || !RegularUtil.isMobile(mMobile!!)) {
                    ToastUtil.show(R.string.input_mobile_error)
                    return
                }
                if (TextUtils.isEmpty(mPassword) || !RegularUtil.isPassword(mPassword!!)) {
                    ToastUtil.show(R.string.input_password_error)
                    return
                }
                userLogin()
            }
            R.id.tv_register_now//立即注册
            -> {
                RegisterActivity.start(mContext!!)
                finish()
            }
            R.id.tv_forget_password//忘记密码
            -> {
            }
        }

    }

    /**
     * login
     */
    private fun userLogin() {
        hideKeyboard()
        SendRequest.userLogin(mContext!!, mMobile!!, mPassword!!, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val resultBean = mGson!!.fromJson(response, JSonResultBean::class.java)
                if (resultBean.isSuccess) {
                    //登录成功
                    mApplication.token = resultBean.data
                    MainActivity.instance?.loadUserInfo()

                    finish()
                } else {
                    //登录失败
                    LogUtil.d("登录失败：" + resultBean.message)
                    ToastUtil.show(resultBean.message)
                }
            }

            override fun onFailure(e: Throwable) {
                LogUtil.d("登陆失败：" + e.message)
                ToastUtil.show(e.toString())
            }
        })
    }

    private val mAuthListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            LogUtil.d(TAG + ": Authorize succeed")
            LogUtil.d(TAG + " " + mGson?.toJson(map))
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            LogUtil.d(TAG + ": Authorize error")
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            LogUtil.d(TAG + ": Authorize cancel")
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
        }
    }

}
