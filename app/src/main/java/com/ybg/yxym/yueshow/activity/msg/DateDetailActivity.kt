package com.ybg.yxym.yueshow.activity.msg

import android.content.Context
import android.content.Intent
import android.view.View
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.UserDate
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_date_detail.*

class DateDetailActivity : BaseActivity() {

    private var dateId = 0L

    override fun setContentViewId(): Int {
        return R.layout.activity_date_detail
    }

    override fun setUpView() {
        setCustomTitle("")
    }

    override fun init() {
        if (intent != null) {
            dateId = intent.extras.getLong("dateId")
            getDateDetail()
        }
    }

    fun acceptDate(view: View) {
        SendRequest.acceptDate(mContext!!, mApplication.token, "$dateId", object :
                OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    ToastUtil.show("操作完成")
                } else {
                    jsonBean.let {
                        ToastUtil.show(jsonBean?.message ?: "获取约会详情失败")
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取约会详情失败")
            }
        })
    }

    fun rejectDate(view: View) {
        SendRequest.rejectDate(mContext!!, mApplication.token, "$dateId", object :
                OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    ToastUtil.show("操作完成")
                } else {
                    jsonBean.let {
                        ToastUtil.show(jsonBean?.message ?: "获取约会详情失败")
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取约会详情失败")
            }
        })
    }

    private fun getDateDetail() {
        SendRequest.viewDateDetail(mContext!!, mApplication.token, "$dateId", object :
                OkCallback<String>(OkStringParser()) {
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val userDate = mGson!!.fromJson<UserDate>(jsonBean.data, object
                        : TypeToken<UserDate>() {}.type)
                    runOnUiThread {
                        title = userDate.fromUser.nickName
                        tv_date_time.text = userDate.createTime
                        //头像
                        val avatar = HttpUrl.getImageUrl(userDate.fromUser.avatar)
                        ImageLoaderUtils.instance.loadBitmap(ci_date_avatar, avatar)
                        tv_date_type.text = userDate.type
                        tv_date_gift.text = userDate.content
                        tv_date_date.text = userDate.dateDate
                    }
                } else {
                    jsonBean.let {
                        ToastUtil.show(jsonBean?.message ?: "获取约会详情失败")
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取约会详情失败")
            }
        })

    }

    companion object {
        fun start(context: Context, dateId: Long) {
            val dateIntent = Intent(context, DateDetailActivity::class.java)
            dateIntent.putExtra("dateId", dateId)
            context.startActivity(dateIntent)
        }
    }
}
