package com.ybg.yxym.yueshow.activity.show

import android.text.TextUtils
import android.view.View
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_show_post.*

/**
 * Created by yangbagang on 2016/11/17.
 */
abstract class PostShowActivity : BaseActivity() {

    protected var title = ""

    fun onClick(view: View) {
        view.isEnabled = false
        //检查数据完整性
        checkAndPost()
    }

    fun checkAndPost() {
        //检查数据完整性
        title = et_show_title.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtil.show("描述不能为空。")
            return
        }
        postShow()
    }

    abstract fun postShow()
}