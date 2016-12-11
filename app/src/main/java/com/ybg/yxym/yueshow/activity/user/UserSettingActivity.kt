package com.ybg.yxym.yueshow.activity.user

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import com.ybg.yxym.yb.utils.AppUtil

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.MainActivity
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.CustomerDialog

/**
 * 类描述：设置中心页面
 */
class UserSettingActivity : BaseActivity() {

    private var tvCaech: TextView? = null
    private var tvCopyright: TextView? = null
    private var switchWifiAuto: Switch? = null
    private var switchNewMsg: Switch? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_user_setting
    }

    override fun setUpView() {
        tvCaech = findViewById(R.id.tv_caech) as TextView
        tvCopyright = findViewById(R.id.tv_copyright) as TextView
        switchWifiAuto = findViewById(R.id.switch_wifi_auto) as Switch
        switchNewMsg = findViewById(R.id.switch_new_msg) as Switch

        mContext = this@UserSettingActivity
        setCustomTitle("设置中心")
    }

    override fun init() {
        if (mApplication.isAutoPlay()) {
            switchWifiAuto?.isChecked = true
        }
        if (mApplication.isReceiverMsg()) {
            switchNewMsg?.isChecked = true
        }
        switchWifiAuto!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mApplication.setAutoPlay(true)
            } else {
                mApplication.setAutoPlay(false)
            }
        }
        switchNewMsg!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mApplication.setReceiverMsg(true)
            } else {
                mApplication.setReceiverMsg(false)
            }
        }
        tvCopyright?.text = AppUtil.getAppVersion(mContext!!, "com.ybg.yxym.yueshow")
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.rl_drafts -> {
                //ToastUtil.show("草稿箱")
            }
            R.id.tv_caech -> {
                //ToastUtil.show("本地缓存")
            }
            R.id.tv_copyright -> {
                //ToastUtil.show("版本更新")
            }
            R.id.rl_agreement -> {
                //ToastUtil.show("协议")
            }
            R.id.tv_logout -> {
                mApplication.token = ""
                MainActivity.instance?.removeUserInfo()
            }
        }
    }

    /**
     * 推出提示
     */
    //    private fun logout() {
//        val builder = CustomerDialog.Builder(mContext!!)
//        builder.setMessage("今天是星期二")
//        builder.setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
//        builder.setNegativeButton("取消", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
//        builder.create().show()
//    }

    companion object {

        /**
         * @param context 跳转到本页面
         */
        fun start(context: Context) {
            val starter = Intent(context, UserSettingActivity::class.java)
            context.startActivity(starter)
        }
    }
}
