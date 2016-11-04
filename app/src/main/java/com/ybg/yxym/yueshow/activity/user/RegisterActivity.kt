package com.ybg.yxym.yueshow.activity.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText

import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.utils.LogUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.MainActivity
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.RegularUtil
import com.ybg.yxym.yueshow.utils.ToastUtil

/**
 * 用户注册页面
 */
class RegisterActivity : BaseActivity() {

    private var m_etRegisterMobile: EditText? = null
    private var m_etRegisterPassword: EditText? = null
    private var m_etRegisterCaptcha: EditText? = null
    private var m_btnGetCaptcha: Button? = null
    private var m_btnRegister: Button? = null

    private var mMobile: String? = null//手机号or悦美号
    private var mPassword: String? = null//登陆密码
    private var mCaptcha: String? = null//验证码

    override fun setContentViewId(): Int {
        return R.layout.activity_register
    }

    override fun setUpView() {
        m_etRegisterMobile = findViewById(R.id.et_register_mobile) as EditText
        m_etRegisterPassword = findViewById(R.id.et_register_password) as EditText
        m_etRegisterCaptcha = findViewById(R.id.et_register_captcha) as EditText
        m_btnGetCaptcha = findViewById(R.id.btn_get_captcha) as Button
        m_btnRegister = findViewById(R.id.btn_register) as Button
    }

    override fun init() {

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_get_captcha//获取验证码
            -> {
                mMobile = getTextViewString(m_etRegisterMobile!!)
                if (TextUtils.isEmpty(mMobile) || !RegularUtil.isMobile(mMobile!!)) {
                    ToastUtil.show(R.string.input_mobile_error)
                    return
                }
                getCaptcha()
                mTimer!!.start()
            }
            R.id.btn_register//注册
            -> {
                mMobile = getTextViewString(m_etRegisterMobile!!)
                mPassword = getTextViewString(m_etRegisterPassword!!)
                mCaptcha = getTextViewString(m_etRegisterCaptcha!!)
                if (TextUtils.isEmpty(mMobile) || !RegularUtil.isMobile(mMobile!!)) {
                    ToastUtil.show(R.string.input_mobile_error)
                    return
                }
                if (TextUtils.isEmpty(mPassword) || !RegularUtil.isPassword(mPassword!!)) {
                    ToastUtil.show(R.string.input_password_error)
                    return
                }
                if (TextUtils.isEmpty(mCaptcha)) {
                    ToastUtil.show(R.string.input_captcha_error)
                    return
                }
                checkCaptcha()
            }
        }// CompleteDataActivity.start(mContext, mMobile, mPassword);
    }

    private var mTimer: CountDownTimer? = object : CountDownTimer((60 * 1000).toLong(), 1000) {
        override fun onTick(l: Long) {
            m_btnGetCaptcha!!.isClickable = false
            m_btnGetCaptcha!!.text = String.format(getString(R.string.get_captcha_later), l / 1000)
            m_btnGetCaptcha!!.setBackgroundResource(R.drawable.shape_btn_b3)
        }

        override fun onFinish() {
            m_btnGetCaptcha!!.isClickable = true
            m_btnGetCaptcha!!.setText(R.string.get_captcha_again)
            m_btnGetCaptcha!!.setBackgroundResource(R.drawable.shape_btn_login)
        }
    }

    private fun getCaptcha() {
        SendRequest.getCaptcha(mContext!!, mMobile!!, object : OkCallback<String>(OkStringParser
        ()) {
            override fun onSuccess(code: Int, s: String) {
                LogUtil.d(TAG + ":get message success " + s)
                ToastUtil.show("验证码已发送到您手机")
            }

            override fun onFailure(e: Throwable) {
                LogUtil.d(TAG + ":get message error code = " + e.message)
                ToastUtil.show(R.string.request_error)
            }
        })
    }

    private fun checkCaptcha() {
        hideKeyboard()
        SendRequest.checkCaptcha(mContext!!, mMobile!!, mCaptcha!!, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val result = mGson!!.fromJson(response, JSonResultBean::class.java)
                if (result != null) {
                    if (result.isSuccess) {
                        //CompleteDataActivity.start(mContext!!, mMobile!!, mPassword!!)
                        userRegister()
                        //finish()
                    } else {
                        ToastUtil.show(result.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show(R.string.request_error)
                ToastUtil.show(e.toString())
            }
        })
    }

    private fun userRegister() {
        SendRequest.userRegister(mContext!!, mMobile!!, mPassword!!, object : OkCallback<String>
        (OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                LogUtil.d(TAG + ": " + response)
                val resultBean = mGson!!.fromJson(response, JSonResultBean::class.java)
                if (resultBean.isSuccess) {
                    mApplication.token = resultBean.data
                    MainActivity.instance?.loadUserInfo()
                    finish()
                } else {
                    LogUtil.d("注册失败：" + resultBean.message)
                    ToastUtil.show(resultBean.message)
                }
            }

            override fun onFailure(e: Throwable) {
                LogUtil.e(TAG + ": " + e.message)
                ToastUtil.show(e.toString())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //完善资料成功后结束当前页面
        if (resultCode == Activity.RESULT_OK && requestCode == IntentExtra.RequestCode.REQUEST_CODE_REGISTER) {
            finish()
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, RegisterActivity::class.java)
            context.startActivity(starter)
        }
    }
}
