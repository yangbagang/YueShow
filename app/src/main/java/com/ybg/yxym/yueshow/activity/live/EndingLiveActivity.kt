package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.view.View
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yb.utils.DateUtil
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_ending_live.*
import org.json.JSONObject

/**
 * 类描述：主播结束页面
 */
class EndingLiveActivity : BaseActivity() {

    private var showId: Long? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_ending_live
    }

    override fun setUpView() {

    }

    override fun init() {
        if (intent != null) {
            showId = intent.extras.getLong("showId")
            getLiveData()
        }
        setCustomTitle("直播结束")
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.iv_share_sina -> ToastUtil.show("分享到新浪微博")
            R.id.iv_share_weixin -> ToastUtil.show("分享到微信")
            R.id.iv_share_friend -> ToastUtil.show("分享到朋友圈")
            R.id.iv_share_qq -> ToastUtil.show("分享到QQ")
            R.id.iv_share_space -> ToastUtil.show("分享到空间")
            R.id.btn_back_home -> finish()
        }
    }

    private fun getLiveData() {
        if (showId == null) {
            return
        }
        SendRequest.getLiveDetail(mContext!!, showId!!, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val json = JSONObject(jsonBean.data)
                    val num = json.getInt("num")
                    val show = mGson!!.fromJson<YueShow>(json.getString("show"), object
                        : TypeToken<YueShow>() {}.type)
                    runOnUiThread {
                        // 观看人数
                        tv_watching_people.text = "$num"
                        // 点赞数量
                        tv_parise.text = "${show?.zanNum}"
                        // 美豆数量
                        tv_get_meidou.text = "0"
                        // 直播时长
                        val timeArray = DateUtil.dateDiff(show!!.createTime!!, show!!.updateTime!!,
                                "yyyy-MM-dd HH:mm:ss")
                        tv_live_time.text = "${timeArray[1]}小时${timeArray[2]}分${timeArray[3]}秒"
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取数据失败")
                if (AppConstants.isDebug) {
                    e.printStackTrace()
                }
            }
        })
    }

    companion object {

        fun start(context: Context, showId: Long) {
            val starter = Intent(context, EndingLiveActivity::class.java)
            starter.putExtra("showId", showId)
            context.startActivity(starter)
        }
    }
}
