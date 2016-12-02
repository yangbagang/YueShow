package com.ybg.yxym.yueshow.activity.home

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.bang.ShowBang
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.http.HttpUrl
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_charts.*

/**
 * 悦美榜
 */
class ChartsFragment : BaseFragment() {

    private lateinit var zhongHuaBang: RelativeLayout
    private lateinit var meiLiBang: RelativeLayout
    private lateinit var renQiBang: RelativeLayout
    private lateinit var huoLiBang: RelativeLayout
    private lateinit var haoQiBang: RelativeLayout

    override fun setContentViewId(): Int {
        return R.layout.fragment_charts
    }

    override fun setUpView() {
        zhongHuaBang = mRootView!!.findViewById(R.id.rl_zh_bang) as RelativeLayout
        meiLiBang = mRootView!!.findViewById(R.id.rl_ml_bang) as RelativeLayout
        renQiBang = mRootView!!.findViewById(R.id.rl_hl_bang) as RelativeLayout
        huoLiBang = mRootView!!.findViewById(R.id.rl_rq_bang) as RelativeLayout
        haoQiBang = mRootView!!.findViewById(R.id.rl_hq_bang) as RelativeLayout

        zhongHuaBang.setOnClickListener {
            //do nothing
        }
        meiLiBang.setOnClickListener {
            ShowBang.start(mContext!!, 1)
        }
        renQiBang.setOnClickListener {
            ShowBang.start(mContext!!, 2)
        }
        huoLiBang.setOnClickListener {
            ShowBang.start(mContext!!, 3)
        }
        haoQiBang.setOnClickListener {
            //do nothing
        }
    }

    override fun init() {
        loadRuiMeiBang()
        loadMeiLiBang()
        loadHuoLiBang()
        loadRenQiBang()
        loadHaoQiBang()
    }

    fun loadRuiMeiBang() {

    }

    fun loadMeiLiBang() {
        SendRequest.getRuiMeiBang(mContext!!, "2016-01-01", "2999-12-31", 1, 3, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val list = mGson!!.fromJson<List<MeiLiItem>>(jsonBean.data, object :
                            TypeToken<List<MeiLiItem>>(){}.type)
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            ImageLoaderUtils.instance.loadBitmap(ml_1_pic, HttpUrl.getImageUrl
                            (first.avatar))
                        } else {
                            fl_ml_1.visibility = View.GONE
                        }
                        if (list.size > 1) {
                            val second = list.get(1)
                            ImageLoaderUtils.instance.loadBitmap(ml_2_pic, HttpUrl.getImageUrl
                            (second.avatar))
                        } else {
                            fl_ml_2.visibility = View.GONE
                        }
                        if (list.size > 2) {
                            val third = list.get(2)
                            ImageLoaderUtils.instance.loadBitmap(ml_3_pic, HttpUrl.getImageUrl
                            (third.avatar))
                        } else {
                            fl_ml_3.visibility = View.GONE
                        }
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取美力榜失败")
            }

        })
    }

    fun loadHuoLiBang() {
        SendRequest.getHuoLiBang(mContext!!, "2016-01-01", "2999-12-31", 1, 3, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val list = mGson!!.fromJson<List<MeiLiItem>>(jsonBean.data, object :
                            TypeToken<List<MeiLiItem>>(){}.type)
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            ImageLoaderUtils.instance.loadBitmap(hl_1_pic, HttpUrl.getImageUrl
                            (first.avatar))
                        } else {
                            fl_hl_1.visibility = View.GONE
                        }
                        if (list.size > 1) {
                            val second = list.get(1)
                            ImageLoaderUtils.instance.loadBitmap(hl_2_pic, HttpUrl.getImageUrl
                            (second.avatar))
                        } else {
                            fl_hl_2.visibility = View.GONE
                        }
                        if (list.size > 2) {
                            val third = list.get(2)
                            ImageLoaderUtils.instance.loadBitmap(hl_3_pic, HttpUrl.getImageUrl
                            (third.avatar))
                        } else {
                            fl_hl_3.visibility = View.GONE
                        }
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取美力榜失败")
            }

        })
    }

    fun loadRenQiBang() {
        SendRequest.getRuiMeiBang(mContext!!, "2016-01-01", "2999-12-31", 1, 3, object :
                OkCallback<String>(OkStringParser()){

            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val list = mGson!!.fromJson<List<MeiLiItem>>(jsonBean.data, object :
                            TypeToken<List<MeiLiItem>>(){}.type)
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            val first = list.first()
                            ImageLoaderUtils.instance.loadBitmap(rq_1_pic, HttpUrl.getImageUrl
                            (first.avatar))
                        } else {
                            fl_rq_1.visibility = View.GONE
                        }
                        if (list.size > 1) {
                            val second = list.get(1)
                            ImageLoaderUtils.instance.loadBitmap(rq_2_pic, HttpUrl.getImageUrl
                            (second.avatar))
                        } else {
                            fl_rq_2.visibility = View.GONE
                        }
                        if (list.size > 2) {
                            val third = list.get(2)
                            ImageLoaderUtils.instance.loadBitmap(rq_3_pic, HttpUrl.getImageUrl
                            (third.avatar))
                        } else {
                            fl_rq_3.visibility = View.GONE
                        }
                    }
                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取美力榜失败")
            }

        })
    }

    fun loadHaoQiBang() {

    }

    companion object {

        fun newInstance(): ChartsFragment {

            val args = Bundle()

            val fragment = ChartsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    data class MeiLiItem(var user_id: Long, var avatar: String, var nickName: String, var
    scoreValue: Int)
}
