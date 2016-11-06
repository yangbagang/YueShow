package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.MainActivity
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.RegularUtil
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 用户登陆界面
 */
class LoginActivity : BaseActivity() {

    private var mMobile: String = ""//手机号or悦美号
    private var mPassword: String = ""//登录密码

    private var mPlatform: SHARE_MEDIA? = null
    private var mShareAPI: UMShareAPI? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_login
    }

    override fun setUpView() {

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
                mMobile = getTextViewString(et_login_mobile)
                mPassword = getTextViewString(et_login_password)
                if (TextUtils.isEmpty(mMobile) || !RegularUtil.isMobile(mMobile)) {
                    ToastUtil.show(R.string.input_mobile_error)
                    return
                }
                if (TextUtils.isEmpty(mPassword) || !RegularUtil.isPassword(mPassword)) {
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
                ToastUtil.show("新功能开发中，敬请期待...")
            }
        }

    }

    /**
     * login
     */
    private fun userLogin() {
        hideKeyboard()
        SendRequest.userLogin(mContext!!, mMobile, mPassword, object : OkCallback<String>
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
                ToastUtil.show("登陆失败，请稍候再试。")
            }
        })
    }

    private val mAuthListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            LogUtil.d(TAG + ": Authorize succeed")
            LogUtil.d(TAG + " " + mGson?.toJson(map))
            var openid = ""
            var nickName = ""
            var sex = ""
            var profile_image_url = ""
            var platform = ""

            when (share_media) {
                SHARE_MEDIA.QQ -> {
                    openid = map["openid"]!!
                    nickName = map["screenname"]!!
                    profile_image_url = map["profile_image_url"]!!
                    sex = map["gender"]!!
                    platform = "QQ"
                }
                SHARE_MEDIA.WEIXIN -> {
                    openid = map["openid"]!!
                    nickName = map["screenname"]!!
                    profile_image_url = map["profile_image_url"]!!
                    sex = map["gender"]!!
                    platform = "WX"
                }
                SHARE_MEDIA.SINA -> {
                    openid = map["id"]!!
                    nickName = map["screen_name"]!!
                    profile_image_url = map["profile_image_url"]!!
                    sex = map["gender"]!!
                    platform = "SINA"
                }
            }
            SendRequest.umLogin(mContext!!, openid, platform, nickName, profile_image_url, sex, object : OkCallback<String>
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
                    ToastUtil.show("登陆失败，请稍候再试。")
                }
            })
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
