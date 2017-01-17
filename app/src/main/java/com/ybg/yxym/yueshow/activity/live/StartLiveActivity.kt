package com.ybg.yxym.yueshow.activity.live

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import com.google.gson.reflect.TypeToken
import com.ybg.yxym.yb.bean.JSonResultBean
import com.ybg.yxym.yb.bean.YueShow
import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.activity.base.BaseActivity
import com.ybg.yxym.yueshow.constant.AppConstants
import com.ybg.yxym.yueshow.constant.IntentExtra
import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.SendRequest
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.UploadListener
import com.ybg.yxym.yueshow.http.parser.OkStringParser
import com.ybg.yxym.yueshow.utils.BitmapUtils
import com.ybg.yxym.yueshow.utils.ImageLoaderUtils
import com.ybg.yxym.yueshow.utils.ToastUtil
import com.ybg.yxym.yueshow.view.gallery.MultiImageSelectorActivity
import kotlinx.android.synthetic.main.activity_enter_av_live.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * 类描述：开启直播页面
 */
class StartLiveActivity : BaseActivity() {

    //自定义的弹出框类
    private var menuWindow: TopicSelectPopupWindow? = null

    private var thumbnail = ""
    private var barId = 1

    override fun setContentViewId(): Int {
        return R.layout.activity_enter_av_live
    }

    override fun setUpView() {

    }

    override fun init() {
        btn_start_live.setOnClickListener(onClickListener)
        btn_edit_cover.setOnClickListener(onClickListener)
        rl_conversation_select.setOnClickListener(onClickListener)

        setCustomTitle("开启直播")
    }

    private fun saveThumbnail(path: String) {

        object : AsyncTask<String, String, String>(){

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (result == null) {
                    return
                }
                uploadThumbnail(result)
            }

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg p0: String?): String {
                if (p0 == null) return ""
                //取得上传目标
                var bitmap = BitmapFactory.decodeFile(path)
                //压缩尺寸
                bitmap = BitmapUtils.resizeImage(bitmap, 300, 300)
                //保存
                return BitmapUtils.saveToFile(AppConstants.IMAGE_CACHE_PATH, true, bitmap)
            }

        }.execute()
    }

    private fun uploadThumbnail(path: String) {
        SendRequest.uploadFile(mContext!!, "avatar", File(path), object: UploadListener(){
            override fun onResponse(call: Call?, response: Response?) {
                response?.let { onSuccess(response) }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.let { onFailure(it) }
            }

            override fun onSuccess(response: Response) {
                val json = JSONObject(response.body().string())
                thumbnail = json.getString("fid")
            }

            override fun onFailure(e: Exception) {
                workInLoopThread {
                    ToastUtil.show("上传头像失败")
                }
            }

            override fun onUIProgress(progress: Progress) {

            }
        })
    }

    private fun createLive() {
        SendRequest.createLive(mContext!!, mApplication.token, "$barId", thumbnail, ed_live_title
                .text.toString(), object : OkCallback<String>(OkStringParser()){
            override fun onSuccess(code: Int, response: String) {
                val jsonBean = JSonResultBean.fromJSON(response)
                if (jsonBean != null && jsonBean.isSuccess) {
                    val json = JSONObject(jsonBean.data)
                    val url = json.getString("url")
                    val show = mGson!!.fromJson<YueShow>(json.getString("show"), object
                        : TypeToken<YueShow>(){}.type)

                } else {
                    jsonBean?.let {
                        ToastUtil.show(jsonBean.message)
                    }
                }
            }

            override fun onFailure(e: Throwable) {
                e.printStackTrace()
                ToastUtil.show("直播开启失败")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        if (requestCode == IntentExtra.RequestCode.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val path = data.getStringExtra(MultiImageSelectorActivity.EXTRA_RESULT)
            if (path == "") return
            ImageLoaderUtils.instance.loadFileBitmap(iv_live_photo, path!!)
            saveThumbnail(path)
        }
    }

    /**
     * 点击事件
     */
    private var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_start_live -> {
                //创建直播
                createLive()
            }
            R.id.btn_edit_cover -> {
                MultiImageSelectorActivity.start(mContext!!, true, 1,
                        MultiImageSelectorActivity.MODE_SINGLE)
            }
            R.id.rl_conversation_select -> {
                menuWindow = TopicSelectPopupWindow(this@StartLiveActivity, onPopItemsClickListener)
                //显示窗口
                menuWindow!!.showAtLocation(this@StartLiveActivity.findViewById(R.id.rl_start_live),
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0) //设置layout在PopupWindow中显示的位置
            }
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
                barId = 1
            }
            R.id.btn_shi_shi -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "温湿暖潮潮"
                barId = 2
            }
            R.id.btn_ren_sheng -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "人生要BIBI"
                barId = 3
            }
            R.id.btn_qi_zhi -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "有颜有气质"
                barId = 4
            }
            R.id.btn_chang_yan -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "唱演兼懂言"
                barId = 5
            }
            R.id.btn_hui_xiu -> {
                menuWindow!!.dismiss()
                tv_live_conversation_select!!.text = "会秀更会美"
                barId = 6
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
            val halfTrans = 0xb0000000
            val dw = ColorDrawable(halfTrans.toInt())
            //设置SelectPicPopupWindow弹出窗体的背景
            this.setBackgroundDrawable(dw)
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, StartLiveActivity::class.java)
            context.startActivity(starter)
        }
    }

}
