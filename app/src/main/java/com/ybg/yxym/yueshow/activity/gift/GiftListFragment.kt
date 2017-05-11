package com.ybg.yxym.yueshow.activity.gift

import android.widget.GridView
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.RuiGift
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseFragment
import com.ybg.yxym.yueshow.adapter.GiftItemAdapter
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.ToastUtil
import java.util.*

/**
 * Created by yangbagang on 2017/4/25.
 */
class GiftListFragment(var userId: Long, var sendMsgFlag: Int, var ymCode: String) : BaseFragment() {

    private var giftList: MutableList<RuiGift> = ArrayList<RuiGift>()
    private lateinit var adapter: GiftItemAdapter

    private lateinit var giftView: GridView

    override fun setContentViewId(): Int {
        return R.layout.fragment_gift_list
    }

    override fun setUpView() {
        giftView = mRootView!!.findViewById(R.id.gv_gifts) as GridView
    }

    override fun init() {
        adapter = GiftItemAdapter(mContext!!)
        giftView.adapter = adapter
        getGiftInfoList()

        giftView.setOnItemClickListener { parent, view, position, id ->
            val gift = giftList[position]
            SendGiftActivity.start(mContext!!, userId, gift.id, sendMsgFlag, ymCode, gift.name, gift.image)
            mContext!!.finish()
        }
    }

    private fun getGiftInfoList() {
        SendRequest.getGiftList(mContext!!, 1, 1000, object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val data = mGson!!.fromJson<List<RuiGift>>(jsonBean.data,
                            object : TypeToken<List<RuiGift>>(){}.type)
                    giftList.clear()
                    giftList.addAll(data)
                    adapter.setData(giftList)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(e: Throwable) {
                ToastUtil.show("获取数据失败")
            }
        })
    }
}