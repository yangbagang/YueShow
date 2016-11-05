package com.ybg.yxym.yueshow.activity.live

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_enter_av_live.*

/**
 * 类描述：开启直播页面
 */
class StartLiveActivity : BaseActivity() {

    //自定义的弹出框类
    private var menuWindow: TopicSelectPopupWindow? = null

    private var userPhone = ""
    private var tls_identify = ""

    protected override fun setContentViewId(): Int {
        return R.layout.activity_enter_av_live
    }

    protected override fun setUpView() {

    }

    protected override fun init() {
        btn_start_live.setOnClickListener(onClickListener)
        btn_edit_cover.setOnClickListener(onClickListener)
        rl_conversation_select.setOnClickListener(onClickListener)
        iv_close.setOnClickListener(onClickListener)

        val preferences = getSharedPreferences(AppConstants.SHARE_PREFERENCE_USER, Context.MODE_PRIVATE)
        userPhone = preferences.getString(AppConstants.USER_NAME, "")
        tls_identify = preferences.getString(AppConstants.USER_TLS_TOKEN, "")

        ToastUtil.show(userPhone + "------------------" + tls_identify)
    }

    /**
     * 点击事件
     */
    private var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_start_live -> ToastUtil.show("点击了开始。。。。。。")
            R.id.btn_edit_cover -> ToastUtil.show("点击了编辑封面!")
            R.id.rl_conversation_select -> {
                menuWindow = TopicSelectPopupWindow(this@StartLiveActivity, onPopItemsClickListener)
                //显示窗口
                menuWindow!!.showAtLocation(this@StartLiveActivity.findViewById(R.id.rl_start_live),
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0) //设置layout在PopupWindow中显示的位置
            }
            R.id.iv_close -> this@StartLiveActivity.finish()
        }
    }

    /**
     * popupwindow点击事件
     */
    internal var onPopItemsClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {

            R.id.btn_dou_meng -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "逗萌乐悠悠"
            }
            R.id.btn_shi_shi -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "温湿暖潮潮"
            }
            R.id.btn_ren_sheng -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "人生要BIBI"
            }
            R.id.btn_qi_zhi -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "有颜有气质"
            }
            R.id.btn_chang_yan -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "唱演兼懂言"
            }
            R.id.btn_hui_xiu -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "会秀更会美"
            }
        }
    }


    /**
     * 直播话题选择 底部弹出pippupwindow
     */
    internal inner class TopicSelectPopupWindow(context: Context, itemsOnClick: View.OnClickListener) : PopupWindow() {

        private val btnDouMeng: Button
        private val btnShiShi: Button
        private val btnRenSheng: Button
        private val btnQiZhi: Button
        private val btnChangYan: Button
        private val btnHuiXiu: Button
        private val btnDismiss: ImageView

        private val mMenuView: View

        init {

            val inflater = context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mMenuView = inflater.inflate(R.layout.item_topic_select_popwindow, null)
            btnDouMeng = mMenuView.findViewById(R.id.btn_dou_meng) as Button
            btnShiShi = mMenuView.findViewById(R.id.btn_shi_shi) as Button
            btnRenSheng = mMenuView.findViewById(R.id.btn_ren_sheng) as Button
            btnQiZhi = mMenuView.findViewById(R.id.btn_qi_zhi) as Button
            btnChangYan = mMenuView.findViewById(R.id.btn_chang_yan) as Button
            btnHuiXiu = mMenuView.findViewById(R.id.btn_hui_xiu) as Button
            btnDismiss = mMenuView.findViewById(R.id.iv_dismiss) as ImageView
            btnDouMeng.setOnClickListener(itemsOnClick)
            btnShiShi.setOnClickListener(itemsOnClick)
            btnRenSheng.setOnClickListener(itemsOnClick)
            btnQiZhi.setOnClickListener(itemsOnClick)
            btnChangYan.setOnClickListener(itemsOnClick)
            btnHuiXiu.setOnClickListener(itemsOnClick)
            //关闭popupwindow
            btnDismiss.setOnClickListener { dismiss() }
            //设置SelectPicPopupWindow的View
            this.contentView = mMenuView
            //设置SelectPicPopupWindow弹出窗体的宽
            this.width = ViewGroup.LayoutParams.MATCH_PARENT
            //设置SelectPicPopupWindow弹出窗体的高
            this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            //设置SelectPicPopupWindow弹出窗体可点击
            this.isFocusable = true
            //设置SelectPicPopupWindow弹出窗体动画效果
            this.animationStyle = R.style.popupwindow_buttom_anim
            //实例化一个ColorDrawable颜色为半透明
            //            ColorDrawable dw = new ColorDrawable(0xb0000000);
            //设置SelectPicPopupWindow弹出窗体的背景
            //            this.setBackgroundDrawable(dw);
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, StartLiveActivity::class.java)
            context.startActivity(starter)
        }
    }


}
